package com.travoca.app.fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.okhttp.ResponseBody;
import com.travoca.api.TravocaApi;
import com.travoca.api.model.SaveRecordResponse;
import com.travoca.api.model.search.ImageRequest;
import com.travoca.app.App;
import com.travoca.app.R;
import com.travoca.app.TravocaApplication;
import com.travoca.app.activity.LoginActivity;
import com.travoca.app.activity.NewRecordActivity;
import com.travoca.app.member.MemberStorage;
import com.travoca.app.member.model.User;
import com.travoca.app.travocaapi.RetrofitCallback;
import com.travoca.app.utils.amazon.UploadService;
import com.travoca.app.widget.ImagePicker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;

/**
 * @author ortal
 * @date 2015-05-17
 */
public class NewRecordFragment extends BaseFragment {

    private static final int REQUEST_PERMISSION_LOCATION = 2;

    private static final int NUMBER_OF_RETRIES = 4;

    public TravocaApi mTravocaApi;

    @Bind(R.id.button3)
    FloatingActionButton playButton;

    @Bind(R.id.button2)
    FloatingActionButton stopButton;

    @Bind(R.id.button)
    FloatingActionButton recordButton;

    @Bind(R.id.button4)
    Button sendButton;

    @Bind(R.id.title)
    MaterialEditText mTitle;

    @Bind(R.id.description)
    MaterialEditText mDescription;

    @Bind(R.id.locationName)
    MaterialEditText mLocationName;

    //    @Bind(R.id.type)
//    MaterialEditText mType;
    @Bind(R.id.image)
    ImageView mImageView;

    private int mNumberRetries = 0;

    private MediaRecorder audioRecorder;

    private String RecordFilePath = null;

    private Listener mListener;

    private Location mLocation;

    private boolean mPlayButtonState = false;

    private boolean mHasImage = false;

    private boolean mHasRecord = false;

    private LocationRequest mLocationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1);

    private ImageRequest imageRequest;

    private File mImageFile;

    private RetrofitCallback<SaveRecordResponse> mResultsCallback
            = new RetrofitCallback<SaveRecordResponse>() {


        @Override
        public void success(final SaveRecordResponse apiResponse, Response response) {

            Intent intent = new Intent(getActivity(), UploadService.class);
            intent.putExtra(UploadService.ARG_FILE_PATH, RecordFilePath);
            intent.putExtra(UploadService.ARG_FILE_NAME, apiResponse.rowId + ".3gp");
            getActivity().startService(intent);

            intent = new Intent(getActivity(), UploadService.class);
            intent.putExtra(UploadService.ARG_FILE_PATH, mImageFile.getPath());
            intent.putExtra(UploadService.ARG_FILE_NAME, apiResponse.rowId + ".jpg");
            getActivity().startService(intent);

            getActivity().finish();

        }

        @Override
        public void failure(ResponseBody response, boolean isOffline) {
            if (response != null) {
            } else {
                retry();
            }
        }

        private void retry() {
            if (mNumberRetries++ < NUMBER_OF_RETRIES) {
                mTravocaApi.saveRecordDetails(imageRequest).enqueue(mResultsCallback);
            }
        }
    };

    public static NewRecordFragment newInstance() {
        NewRecordFragment fragment = new NewRecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_record, container, false);
        ButterKnife.bind(this, view);

//        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        RecordFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/recording.3gp";
        mTravocaApi = TravocaApplication.provide(getActivity()).travocaApi();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCurrentLocation();
                getActivity().startActivityForResult(ImagePicker.getPickImageIntent(getActivity()),
                        NewRecordActivity.PICK_IMAGE_ID);
            }
        });
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayButtonState) {
                    playButton.setEnabled(false);
                    mPlayButtonState = true;
                    Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_LONG).show();
                    recordButton.setImageDrawable(
                            getActivity().getDrawable(android.R.drawable.ic_media_pause));
                    try {
                        new File(RecordFilePath).delete();
                        audioRecorder = new MediaRecorder();
                        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                        audioRecorder.setOutputFile(RecordFilePath);
                        audioRecorder.prepare();
                        audioRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mHasRecord = true;
                    playButton.setEnabled(true);
                    mPlayButtonState = false;
                    Toast.makeText(getActivity(), "Recording stop", Toast.LENGTH_LONG).show();
                    recordButton.setImageDrawable(
                            getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
                    try {
                        audioRecorder.stop();
                        audioRecorder.release();
                        audioRecorder = null;
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                audioRecorder.stop();
//                audioRecorder.release();
//                audioRecorder = null;

//                stopButton.setEnabled(false);
//                playButton.setEnabled(true);

                Toast.makeText(getActivity(), "Audio recorded successfully", Toast.LENGTH_LONG)
                        .show();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
                    throws IllegalArgumentException, SecurityException, IllegalStateException {
                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(RecordFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getActivity(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (mTitle.length() < 4) {
                    Toast.makeText(getActivity(), "Title Not valid", Toast.LENGTH_LONG).show();
                } else if (mDescription.length() < 4) {
                    Toast.makeText(getActivity(), "Description Not valid", Toast.LENGTH_LONG)
                            .show();
                } else if (mLocationName.length() < 4) {
                    Toast.makeText(getActivity(), "Location NameNot valid", Toast.LENGTH_LONG)
                            .show();
                } else if (!mHasImage) {
                    Toast.makeText(getActivity(), "Image Not valid", Toast.LENGTH_LONG).show();
                } else if (!mHasRecord) {
                    Toast.makeText(getActivity(), "Record Not valid", Toast.LENGTH_LONG).show();
                } else {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    ((NewRecordActivity) getActivity()).getSelectedBitmap()
                            .compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                    MemberStorage memberStorage = App.provide(getActivity()).memberStorage();
                    User user = memberStorage.loadUser();
                    if (user != null) {
                        sendButton.setEnabled(false);
                        byte[] bitmapdata = byteArrayOutputStream.toByteArray();
                        mImageFile = new File(getActivity().getCacheDir(), "image.png");
                        try {
                            mImageFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(mImageFile);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imageRequest = new ImageRequest(mTitle.getText().toString(),
                                mDescription.getText().toString(),
                                mLocationName.getText().toString(),
                                String.valueOf(mLocation.getLatitude()),
                                String.valueOf(mLocation.getLongitude()), "free", user.id);

                        mTravocaApi.saveRecordDetails(imageRequest).enqueue(mResultsCallback);

                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Alert")
                                .setMessage("You must login to upload record")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(LoginActivity.createIntent(getActivity()));
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                }
            }
        });

        return view;
    }

    private void findCurrentLocation() {
        if (ActivityCompat
                .checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mListener.getGoogleApiClient(),
                    mLocationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(android.location.Location location) {
                            mLocation = location;
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    public void setImage(Bitmap selectedBitmap) {
        mImageView.setImageBitmap(selectedBitmap);
        mHasImage = true;
    }

    public void setImage(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
        mHasImage = false;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findCurrentLocation();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    public interface Listener {

        GoogleApiClient getGoogleApiClient();

    }


}
