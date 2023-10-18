package com.ccs114.fisda;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
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

        bindData.btnCamera.setOnClickListener(view1 -> {
//            // Check if both CAMERA and WRITE_EXTERNAL_STORAGE permissions are granted
//            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                // Both permissions are granted, so you can launch the camera
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
//            } else {
//                // Request both CAMERA and WRITE_EXTERNAL_STORAGE permissions
//                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
//            }
// Check if both CAMERA and WRITE_EXTERNAL_STORAGE permissions are granted
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Both permissions are granted, so you can launch the camera

                    dispatchTakePictureIntent();

            } else {
                // Request both CAMERA and WRITE_EXTERNAL_STORAGE permissions
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            }


        });

        bindData.btnGallery.setOnClickListener(view12 -> {
            //Launches Gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
        });

        return view;
    }

    private void dispatchTakePictureIntent()  {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "No file", Toast.LENGTH_LONG).show();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.ccs114.fisda.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                galleryAddPic();

            } else {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "FISDA_" + timeStamp + "_";
        File storageDir = new File(getContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "FiSDA");

        if (! storageDir.exists()){
            if (! storageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("CurrentPath", currentPhotoPath);
        return image;
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
            Toast.makeText(getContext(), "activity ok", Toast.LENGTH_LONG).show();

            Bitmap image = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // If requestCode is REQUEST_IMAGE_CAPTURE, it means the image was taken with the camera directly
//                image = (Bitmap) data.getExtras().get("data");
               // Get the bitmap from the captured image
                try {
                    image = BitmapFactory.decodeFile(currentPhotoPath);
                    Toast.makeText(getContext(), "success bitmap", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "failed bitmap", Toast.LENGTH_LONG).show();

                }
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                // If the requestCode is REQUEST_PICK_IMAGE, it means the image was picked from the gallery
                Uri dat = data.getData();
                currentPhotoPath = getImagePathFromUri(dat);
                imageFileName = getImageFileName(currentPhotoPath);


                try {
                    image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (image != null) {
                // Save the image to a file
//                String imagepath = moveImageToCustomFolder();
//                Log.d("Destination_file", "path: " + imagepath);
                String imagepath = currentPhotoPath;

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
                outputFragment.setArguments(fishInputInfo(byteArray, topFishSpecies, topConfidences, imagepath));

                displayFishInfo(outputFragment);

            } else {
                Log.d("ActivityResult", "Failed to retrieve the image.");
            }
        }
        else {
                if(resultCode != Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "activity not ok" + resultCode, Toast.LENGTH_LONG).show();
                } else if (data == null) {
                    Toast.makeText(getContext(), "data is null", Toast.LENGTH_LONG).show();

                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getImagePathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
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
    private Bundle fishInputInfo(byte[] fishByteArray, String[] topFishSpecies, String[] topConfidences, String imagepath) {
        Bundle args = new Bundle();

        args.putByteArray("imagebytes", fishByteArray);
        args.putStringArray("topFishSpecies", topFishSpecies);
        args.putStringArray("topConfidences", topConfidences);
        args.putString("imagepath", imagepath);
        args.putString("filename", imageFileName);
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
     * Saves the user-selected image to a file and returns the file's new path
     *
     * @param bitmap The image selected by the user
     * @return a String containing the new file path of the image captured from the camera
     */
    private String saveImageToFile(Bitmap bitmap) {
        File backupFile;
        File appFolder;
        String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FiSDA";
        appFolder = new File(path);
        if (!appFolder.exists()) {
            appFolder.mkdir();
            Toast.makeText(getContext(), "Created", Toast.LENGTH_LONG).show();
        }

        String fileName = "picture.jpg";
        backupFile = new File(appFolder, fileName);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(backupFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output.toString();
    }



    public String moveImageToCustomFolder() {
        File sourceDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/temp");
        File destinationDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FiSDA");

        // Check if the source directory exists and is a directory
        if (sourceDirectory.exists() && sourceDirectory.isDirectory()) {
            // Ensure that the destination directory exists; if not, create it
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs();
            }

            // List the files in the source directory
            File[] sourceFiles = sourceDirectory.listFiles();

            // Check if there is exactly one file in the source directory
            if (sourceFiles != null && sourceFiles.length == 1) {
                // Get the source file (the only file in the directory)
                File sourceFile = sourceFiles[0];

                // Create a File object for the destination file
                File destinationFile = new File(destinationDirectory, sourceFile.getName());

                // Perform the file move operation
                if (sourceFile.renameTo(destinationFile)) {
                    // Return the new file path

                    return destinationFile.getAbsolutePath();
                } else {
                    // Handle the case where the file couldn't be moved
                    return null;
                }
            } else {
                // Handle the case where there is not exactly one file in the source directory
                return null;
            }
        } else {
            // Handle the case where the source directory doesn't exist or is not a directory
            return null;
        }
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
