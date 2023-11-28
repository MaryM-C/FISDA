package com.ccs114.fisda.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//Data Binding
import androidx.databinding.DataBindingUtil;

import com.ccs114.fisda.utils.OutputHandler;
import com.ccs114.fisda.R;
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
    private static final int REQUEST_CAMERA_STORAGE_PERMISSION = 101;
    private final int imageSize = 224;

    String imageFileName;

    String currentPhotoPath;
    Uri photoURI = null;

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

        bindData.btnCamera.setOnClickListener(view1 -> askPermissions());

        bindData.btnGallery.setOnClickListener(view1 -> {
            //Launches Gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
        });

        return view;
    }

    void askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check for CAMERA permission
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                // CAMERA permission is granted, launch the camera
                dispatchTakePictureIntent();
            } else {
                // Request CAMERA permission
                requestCameraPermission();
            }
        } else {
            // Check for CAMERA and WRITE_EXTERNAL_STORAGE permissions
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                // Both permissions are granted, launch the camera
                dispatchTakePictureIntent();
            } else {
                // Request CAMERA and WRITE_EXTERNAL_STORAGE permissions
                requestCameraAndStoragePermissions();
            }
        }
    }

    private void requestCameraAndStoragePermissions() {
        requestPermissions(
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CAMERA_STORAGE_PERMISSION);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // Check if CAMERA permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // CAMERA permission granted, launch the camera
                dispatchTakePictureIntent();
            } else {
                // CAMERA permission not granted, handle accordingly
                // You may want to show a message or request the permission again
                requestCameraPermission();
            }
        } else if (requestCode == REQUEST_CAMERA_STORAGE_PERMISSION) {
            // Check if both CAMERA and WRITE_EXTERNAL_STORAGE permissions are granted
            if (grantResults.length > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Both permissions granted, launch the camera
                dispatchTakePictureIntent();
            } else {
                // One or both permissions not granted, handle accordingly
                // You may want to show a message or request the permissions again
                requestCameraAndStoragePermissions();
            }
        }
    }




    public void dispatchTakePictureIntent()  {
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
                photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.ccs114.fisda.fileprovider", photoFile);
                requireContext().grantUriPermission("com.ccs114.fisda", photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                scanFile(photoFile);
            }
        }
    }

    File createImageFile() throws IOException {
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
    void scanFile(File file) {
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

            if(requestCode == REQUEST_PICK_IMAGE) {
                photoURI = Objects.requireNonNull(data).getData();
                handleImagePick(photoURI);
            }

            if(photoURI != null) {
                try {
                    prepareImageForDisplay(photoURI);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.d("ActivityResult", "Failed to retrieve the image.");
            }
        } else {
            handleActivityResultFailure(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImagePick(Uri dat) {
        Bitmap image;

        try {
            image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), dat);
            currentPhotoPath = saveImageToCameraDirectory(image);
            imageFileName = getImageFileName(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
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

    private void handleActivityResultFailure(Intent data) {
        if (data == null) {
            Toast.makeText(getContext(), "Failed to retrieve image data", Toast.LENGTH_LONG).show();
        }
    }

    private void prepareImageForDisplay(Uri photoURI) throws IOException {
        //Todo remove thumbnail

        Bitmap image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), photoURI);
        Bitmap testImage = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);

        OutputHandler handler = new OutputHandler();
        int[] topIndices = classifyImageConcurrent(handler, testImage);
        String[] topFishSpecies = OutputHandler.getTopFishSpeciesName(topIndices);
        Log.d("topindices", String.valueOf(handler.getConfidence()[0]));
        String[] topConfidencesString = OutputHandler.getConfidencesAsFormattedString(handler.getConfidence(), topIndices);

        OutputFragment outputFragment = new OutputFragment();
        outputFragment.setArguments(fishInputInfo(photoURI.toString(), topFishSpecies, topConfidencesString,
                currentPhotoPath, imageFileName, handler));

        displayFishInfo(outputFragment);
    }

    /**
     * Constructs a Bundle containing information about the captured fish image and its classification results.
     *
     * @param topFishSpecies An array containing the top 3 fish species names with the highest confidence.
     * @param topConfidences An array containing the corresponding top 3 confidence scores for each fish species.
     * @param imageFileName assigned filename of the image
     * @return A Bundle containing the image byte array, top fish species names, and confidence scores.
     */
    @NonNull
    private Bundle fishInputInfo(String photoURI, String[] topFishSpecies, String[] topConfidences,
                         String imagepath, String imageFileName, OutputHandler handler) {
        Bundle args = new Bundle();
        args.putString("imagePath", imagepath);
        args.putString("uri", photoURI);
        args.putStringArray("topFishSpecies", topFishSpecies);
        args.putStringArray("topConfidences", topConfidences);
        args.putString("filename", imageFileName);

        if(handler.computeTopConfidence(handler.getConfidence()) < 0.60) {
            args.putBoolean("isNotFish", true);
        }
        return args;
    }
    private int[] classifyImageConcurrent(final OutputHandler handler, final Bitmap image) {
        long startTime = System.currentTimeMillis();
        int[] topPredictions = new int[3];

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<int[]> future = executor.submit(() -> classifyImage(handler, image));

            // Wait for the result with a timeout (adjust the timeout value as needed)
            topPredictions = future.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.e("ERROR", "Error during image classification: " + e.getMessage());
        } finally {
            executor.shutdown(); // Shutdown the executor to release resources
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        Log.d("FunctionTime", "Time taken: " + duration + " milliseconds");

        return topPredictions;
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
                    byteBuffer.putFloat((val >> 31) * (1.f / 255));
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
            topPredictions = OutputHandler.computeTopIndices(handler.getConfidence());

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
