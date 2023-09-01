package com.ccs114.fisda;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// the modified model
import com.ccs114.fisda.ml.FishdaModelV1;

/**
 * This fragment allows users to capture images and classify fish species using a machine learning model.
 */
public class CaptureFragment extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PICK_IMAGE = 1;
    private final int imageSize = 224;

    /**
     * Inflates the layout for the CaptureFragment, initializes UI elements, and sets up click listeners
     * for the camera and gallery buttons.
     *
     * @param inflater           The LayoutInflater used to inflate the layout.
     * @param container          The parent view that the fragment UI will be attached to.
     * @param savedInstanceState The saved state of the fragment (unused in this method).
     * @return The root view of the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);

        Button camera = view.findViewById(R.id.btnCamera);
        Button gallery = view.findViewById(R.id.btnGallery);

        TextView result = view.findViewById(R.id.txtFishName);
        ImageView imageView = view.findViewById(R.id.imgFish);

        camera.setOnClickListener(view1 -> {
            //Asks permission to launch the camera
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        gallery.setOnClickListener(view12 -> {
            //Launches Gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
        });

        return view;
    }

    /**
    * Handles the result of the camera or gallery activity and processes the selected image.
    *
    * @param requestCode The request code indicating the source of the image (camera or gallery).
    * @param resultCode  The result code indicating the success or failure of the activity.
    * @param data        The intent containing the captured or picked image.
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getActivity();

        if (resultCode == Activity.RESULT_OK && data != null) {
            Bitmap image = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // If requestCode is REQUEST_IMAGE_CAPTURE, it means the image was taken with the camera directly
                image = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                // If the requestCode is REQUEST_PICK_IMAGE, it means the image was picked from the gallery
                Uri dat = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (image != null) {
                // Save the image to a file
                String imagePath = saveImageToFile(image);

                //Classify the fish and return the top three results
                Bitmap testImage = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);
                OutputHandler handler = classifyImage(testImage);
                String[] topFishSpecies = handler.getTop3FishSpecies();
                String[] topConfidences = handler.getConfidences();

                //Convert the image to byteArray to pass it to the OutputFragment
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Pass the data to the OutputFragment
                OutputFragment outputFragment = new OutputFragment();
                outputFragment.setArguments(fishInputInfo(byteArray, topFishSpecies, topConfidences));

                displayFishInfo(outputFragment);

            } else {
                Log.d("ActivityResult", "Failed to retrieve the image.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Constructs a Bundle containing information about the captured fish image and its classification results.
     *
     * @param fishByteArray  Byte array representing the fish image.
     * @param topFishSpecies An array containing the top 3 fish species names with the highest confidence.
     * @param topConfidences  An array containing the corresponding top 3 confidence scores for each fish species.
     * @return A Bundle containing the image byte array, top fish species names, and confidence scores.
     */
    @NonNull
    private Bundle fishInputInfo(byte[] fishByteArray, String[] topFishSpecies, String[] topConfidences) {
        Bundle args = new Bundle();
        args.putByteArray("imagebytes", fishByteArray);
        args.putStringArray("topFishSpecies", topFishSpecies);
        args.putStringArray("topConfidences", topConfidences);
        logBundleContents(args);

        return args;
    }

    /**
     * Classifies a fish image using a machine learning model and returns the top three predicted results.
     *
     * @param image The fish image to be classified.
     * @return An OutputHandler containing the top three fish species predictions and their confidence scores.
     */
    private OutputHandler classifyImage(Bitmap image) {
        OutputHandler handler = null;

        try {
            FishdaModelV1 model = FishdaModelV1.newInstance(requireContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0,0, image.getWidth(), image.getHeight());
            int pixel = 0;

            // Iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer
            for(int i = 0; i < imageSize; i++) {
                for(int j = 0; j< imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val >> 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            FishdaModelV1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();

            // Releases model resources if no longer used.
            model.close();

            handler = new OutputHandler(confidence);

        } catch (IOException e) {
            Log.e("ERROR", "Failed to load model/file" + e.getMessage());
        }
        return handler;
    }

    /**
     * Serves the user-selected image to a file and returns the file's new path
     *
     * @param bitmap The image selected by the user
     * @return a String containing the new file path of the image captured from the camera
     */
    private String saveImageToFile(Bitmap bitmap) {
        //TODO Capture images using Camera should save the image
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + "image.jpg";
        try {
            FileOutputStream outputStream = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    /**
     * Logs the contents of the fish information bundle before passing it to the OutputFragment
     *
     * @param args The bundle containing the fish-related information
     */
    private void logBundleContents(Bundle args) {
        for (String key : args.keySet()) {
            Object value = args.get(key);
            Log.d("BundleContents", key + ": " + value);
        }
    }

    /**
     * Displays fish-related information by replacing the current fragment with the specified OutputFragment.
     *
     * @param outputFragment The OutputFragment to be displayed.
     */
    private void displayFishInfo(OutputFragment outputFragment) {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, outputFragment);
        transaction.commit();
    }
}
