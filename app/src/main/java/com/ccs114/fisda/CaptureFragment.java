package com.ccs114.fisda;

import android.Manifest;
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

import com.ccs114.fisda.ml.FishdaModelV1;

public class CaptureFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PICK_IMAGE = 1;
    private Button camera, gallery;
    private ImageView imageView;
    private TextView result, text1;
    private int imageSize = 224;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);

        camera = view.findViewById(R.id.btnCamera);
        gallery = view.findViewById(R.id.btnGallery);

        result = view.findViewById(R.id.txtFishName);
        imageView = view.findViewById(R.id.imgFish);

        text1 = view.findViewById(R.id.textView);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("ActivityResult", "requestCode: " + requestCode);
        Log.d("ActivityResult", "resultCode: " + resultCode);

        if (resultCode == getActivity().RESULT_OK && data != null) {
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

                image = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);

                OutputHandler handler = classifyImage(image);
                String[] topFishSpecies = handler.getTop3FishSpecies();
                String[] topConfidences = handler.getConfidences();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Create a bundle to pass data to the OutputFragment
                Bundle args = new Bundle();
                args.putByteArray("imagebytes", byteArray);
                args.putStringArray("topFishSpecies", topFishSpecies);
                args.putStringArray("topConfidences", topConfidences); // Implement this method
                Log.d("Prediction", topFishSpecies[0]+ " " + topConfidences[0]);
                Log.d("Test", "went here");

                if (args != null) {
                    for (String key : args.keySet()) {
                        Object value = args.get(key);
                        Log.d("BundleContents", key + ": " + value);
                    }
                }

                // Create the OutputFragment and set the arguments
                OutputFragment outputFragment = new OutputFragment();
                outputFragment.setArguments(args);

                FragmentManager manager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, outputFragment); // Replace 'R.id.container' with your fragment container ID
                transaction.commit();

            } else {
                Log.d("ActivityResult", "Failed to retrieve the image.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

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
            // TODO Handle the exception
        }
        return handler;
    }
    //TODO Capture images using Camera should save the image
    // Method to save the image to a file and return the file path
    private String saveImageToFile(Bitmap bitmap) {
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
}
