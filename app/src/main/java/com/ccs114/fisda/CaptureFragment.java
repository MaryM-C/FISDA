package com.ccs114.fisda;

import static java.util.Objects.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//Data Binding
import androidx.databinding.DataBindingUtil;
import com.ccs114.fisda.databinding.FragmentCaptureBinding;

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

    String imageFileName;

    String currentPhotoPath;


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
        FragmentCaptureBinding bindData =
                DataBindingUtil.inflate(inflater, R.layout.fragment_capture,  container, false);

        View view = bindData.getRoot();

        bindData.btnCamera.setOnClickListener(view1 -> checkAndRequestCamaeraPermissions());

        bindData.btnGallery.setOnClickListener(view1 -> {
            //Launches Gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
        });

        return view;
    }

    private void checkAndRequestCamaeraPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Both permissions are granted, so you can launch the camera
                dispatchTakePictureIntent();
            }
        }
        else if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&  ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                dispatchTakePictureIntent();
        }
        else {
            // Request both CAMERA and WRITE_EXTERNAL_STORAGE permissions
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void dispatchTakePictureIntent()  {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.ccs114.fisda.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                scanFile(photoFile);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "FiSDA" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    /**
     * Sents an alert to MediaScannerConnection that a new files has been created
     *
     * @param file the new created image from Camera
     */
    private void scanFile(File file) {
        String mimeType  ="image/jpeg";
        MediaScannerConnection
                .scanFile(getContext(), new String[] {file.getAbsolutePath()},
                        new String[] {mimeType}, null);
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
        if (resultCode == Activity.RESULT_OK) {
            Bitmap image = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                image = handleImageCapture();
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                image = handleImagePick(requireNonNull(data));
            }

            if (image != null) {
                prepareImageForDisplay(image);
            } else {
                Log.d("ActivityResult", "Failed to retrieve the image.");
            }
        } else {
            handleActivityResultFailure(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleActivityResultFailure(Intent data) {
        if (data == null) {
            Toast.makeText(getContext(), "Failed to retrieve image data", Toast.LENGTH_LONG).show();
        }
    }

    private void prepareImageForDisplay(Bitmap image) {
        Bitmap testImage = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);
        OutputHandler handler = new OutputHandler();
        int[] topIndices = classifyImage(handler, testImage);
        String[] topFishSpecies = OutputHandler.getTopFishSpeciesName(topIndices);
        String[] topConfidencesString = OutputHandler.getConfidencesAsFormattedString(handler.getConfidence(), topIndices);

        byte[] byteArray = convertImageToByteArray(image);
        OutputFragment outputFragment = new OutputFragment();
        outputFragment.setArguments(fishInputInfo(byteArray, topFishSpecies, topConfidencesString, currentPhotoPath, handler));

        displayFishInfo(outputFragment);
    }

    private byte[] convertImageToByteArray(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

    private Bitmap handleImagePick(Intent data) {
        Bitmap image = null;
        Uri dat = data.getData();

        try {
            image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), dat);
            currentPhotoPath = saveImageToCameraDirectory(image);
            imageFileName = getImageFileName(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private Bitmap handleImageCapture() {
        Bitmap image = null;

        try {
            image = BitmapFactory.decodeFile(currentPhotoPath);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to Capture Image", Toast.LENGTH_LONG).show();
        }

        imageFileName = getImageFileName(currentPhotoPath);
        return image;
    }

    // Save the image in the same directory as camera images and return the new path
    private String saveImageToCameraDirectory(Bitmap image) throws IOException {
            // Create a new file in the camera directory with the provided fileName
            File imageFile = createImageFile();

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                // Notify the media scanner to scan the new image file
                scanFile(imageFile);

                // Return the path of the saved image
                return imageFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SaveImage", "Failed to save image: " + e.getMessage());
                return null;
            }
    }

    // Function to get the filename from the file path
    private String getImageFileName(String imagePath) {
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            return imageFile.getName();
        }
        return null;
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
    private Bundle fishInputInfo(byte[] fishByteArray, String[] topFishSpecies, String[] topConfidences, String imagepath, OutputHandler handler) {
        Bundle args = new Bundle();

        args.putByteArray("imagebytes", fishByteArray);
        args.putStringArray("topFishSpecies", topFishSpecies);
        args.putStringArray("topConfidences", topConfidences);
        args.putString("imagepath", imagepath);
        args.putString("filename", imageFileName);

        Log.d("top confidence", String.valueOf(handler.computeTopConfidence(handler.getConfidence())));

        if(handler.computeTopConfidence(handler.getConfidence()) < 0.60) {
            args.putBoolean("isNotFish", true);
        }

        logBundleContents(args);

        return args;
    }

    /**
     * Classifies a fish image using a machine learning model and returns the top three predicted results.
     *
     * @param image The fish image to be classified.
     * @return An OutputHandler containing the top three fish species predictions and their confidence scores.
     */
    private int[] classifyImage(OutputHandler handler, Bitmap image) {
        int[] topPredictions = new int[3];

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

            handler.setConfidence(confidence);
            Log.d("Top Confidences[0]", String.valueOf(handler.getConfidence()[0]));
            topPredictions = handler.computeTopIndices(handler.getConfidence());

        } catch (IOException e) {
            Log.e("ERROR", "Failed to load model/file" + e.getMessage());
        }
        return topPredictions;
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
        transaction.replace(R.id.container, outputFragment, "OutputFragment");
        transaction.addToBackStack("OutputFragment");
        transaction.commit();
    }
}
