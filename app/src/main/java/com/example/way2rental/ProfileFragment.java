package com.example.way2rental;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.way2rental.api.ApiService;
import com.example.way2rental.api.RetrofitClient;
import com.example.way2rental.databinding.FragmentProfileBinding;
import com.example.way2rental.model.APIResponse;
import com.example.way2rental.model.FileUploadRequest;
import com.example.way2rental.model.ProviderType;
import com.example.way2rental.model.StatusResponse;
import com.example.way2rental.model.UserProfile;
import com.example.way2rental.ui.auth.LoginActivity;
import com.example.way2rental.ui.profile.EditProfileActivity; // Added
import com.example.way2rental.utility.ToastUtils;
import com.google.gson.Gson; // Added

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private ApiService apiService;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> editProfileLauncher; // Added
    private UserProfile currentUserProfile; // Added

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        }

        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        if (binding != null && getContext() != null) {
                            Glide.with(ProfileFragment.this)
                                 .load(selectedImageUri)
                                 .circleCrop()
                                 .placeholder(android.R.drawable.sym_def_app_icon)
                                 .error(android.R.drawable.sym_def_app_icon)
                                 .into(binding.ivProfileAvatar);
                            uploadFileAndLinkToProfile(selectedImageUri);
                        }
                    }
                }
            }
        );

        // Added launcher for EditProfileActivity
        editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Profile was successfully updated, refresh the data
                    fetchUserProfile();
                    ToastUtils.show(getContext(), "Profile updated!");
                }
            }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUserProfile();
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.ivProfileAvatar.setOnClickListener(v -> showChangePhotoDialog());

        binding.menuItemLogout.setOnClickListener(v -> {
            if (getActivity() == null) return;
            LoginActivity.clearAuthDetails(requireActivity());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // Modified click listener for Edit Profile
        binding.menuItemEditProfile.setOnClickListener(v -> {
            if (currentUserProfile != null && getContext() != null) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra(EditProfileActivity.EXTRA_USER_PROFILE, new Gson().toJson(currentUserProfile));
                editProfileLauncher.launch(intent);
            } else {
                ToastUtils.show(getContext(), "Profile data not loaded yet. Please wait or try again.");
            }
        });

        binding.menuItemAccountSettings.setOnClickListener(v ->
            ToastUtils.show(getContext(), "Account Settings clicked (not implemented)"));
        binding.menuItemMyListings.setOnClickListener(v ->
            ToastUtils.show(getContext(), "My Listings clicked (not implemented)"));
        binding.menuItemNotifications.setOnClickListener(v ->
            ToastUtils.show(getContext(), "Notifications clicked (not implemented)"));
        binding.menuItemHelpSupport.setOnClickListener(v ->
            ToastUtils.show(getContext(), "Help & Support clicked (not implemented)"));
    }

    private void showChangePhotoDialog() {
        if (getContext() == null) return;
        final CharSequence[] options = {"Choose from Gallery", "Remove Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Profile Photo");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Choose from Gallery")) {
                launchGalleryPicker();
            } else if (options[item].equals("Remove Photo")) {
                removeProfilePhoto();
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void launchGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private File getFileFromUri(Uri uri) {
        if (getContext() == null || uri == null) return null;
        File cacheDir = getContext().getCacheDir();
        if (cacheDir == null) return null;

        String fileRealName = "profile_pic_" + System.currentTimeMillis();
        String mimeType = getContext().getContentResolver().getType(uri);
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (extension != null) {
            fileRealName += "." + extension;
        } else {
            fileRealName += ".tmp";
        }

        File tempFile = new File(cacheDir, fileRealName);

        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            if (inputStream == null) return null;
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            return tempFile;
        } catch (IOException e) {
            Log.e(TAG, "Failed to create temp file from URI", e);
            ToastUtils.show(getContext(), "Error preparing image for upload.");
            return null;
        }
    }

    public static String getMimeTypeFromUri(Context context, Uri uri) {
        String mimeType = null;
        if (uri.getScheme() != null && uri.getScheme().equals(android.content.ContentResolver.SCHEME_CONTENT)) {
            mimeType = context.getContentResolver().getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (fileExtension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
            }
        }
        return mimeType != null ? mimeType : "application/octet-stream";
    }

    private void uploadFileAndLinkToProfile(Uri fileUri) {
        if (getContext() == null || apiService == null) {
            ToastUtils.show(getContext(), "Service not available.");
            return;
        }

        File imageFile = getFileFromUri(fileUri);
        if (imageFile == null) {
            ToastUtils.show(getContext(), "Could not prepare image for upload.");
            return;
        }
        if (binding == null) return;
        binding.pbProfileLoading.setVisibility(View.VISIBLE);

        FileUploadRequest requestPartObject = new FileUploadRequest();
        requestPartObject.setFileName(imageFile.getName());
        requestPartObject.setFileType("IMAGE");
        requestPartObject.setFileSize(String.valueOf(imageFile.length()));
        requestPartObject.setProvideType(ProviderType.DB);

        String actualMimeType = getMimeTypeFromUri(getContext(), fileUri);
        RequestBody fileRequestBody = RequestBody.create(imageFile, MediaType.parse(actualMimeType));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("filePart", imageFile.getName(), fileRequestBody);

        File finalImageFile = imageFile;

        apiService.uploadProfilePicture(requestPartObject, filePart).enqueue(new Callback<APIResponse<StatusResponse>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<StatusResponse>> call, @NonNull Response<APIResponse<StatusResponse>> response) {
                if (binding == null || getContext() == null) {
                    if(finalImageFile.exists()) finalImageFile.delete();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<StatusResponse> outerApiResponse = response.body();
                    if (outerApiResponse.getData() != null) {
                        StatusResponse uploadStatusData = outerApiResponse.getData();
                        if (outerApiResponse.getStatus() == 200 && "SUCCESS".equalsIgnoreCase(uploadStatusData.getStatus())) {
                            String referenceId = uploadStatusData.getReferenceId();
                            Log.d(TAG, "File Upload to FileManager successful. Reference ID: " + referenceId);

                            if (TextUtils.isEmpty(referenceId)) {
                                ToastUtils.show(getContext(), "File uploaded, but reference ID is missing.");
                                binding.pbProfileLoading.setVisibility(View.GONE);
                                if(finalImageFile.exists()) finalImageFile.delete();
                                return;
                            }
                            String profilePictureUrl = RetrofitClient.BASE_URL + "fileManager/PUBLIC/download/" + referenceId;
                            linkImageToUserProfile(profilePictureUrl, finalImageFile);

                        } else {
                            binding.pbProfileLoading.setVisibility(View.GONE);
                            ToastUtils.show(getContext(), "File upload failed: " + (uploadStatusData.getMessage() != null ? uploadStatusData.getMessage() : outerApiResponse.getMessage()));
                            if(finalImageFile.exists()) finalImageFile.delete();
                        }
                    } else {
                         binding.pbProfileLoading.setVisibility(View.GONE);
                         ToastUtils.show(getContext(), "File upload response data is null: " + outerApiResponse.getMessage());
                         if(finalImageFile.exists()) finalImageFile.delete();
                    }
                } else {
                    binding.pbProfileLoading.setVisibility(View.GONE);
                    ToastUtils.show(getContext(), "File upload failed. Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "File upload error: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading file upload error body", e);
                    }
                    if(finalImageFile.exists()) finalImageFile.delete();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<StatusResponse>> call, @NonNull Throwable t) {
                if (binding == null || getContext() == null) {
                     if(finalImageFile.exists()) finalImageFile.delete();
                    return;
                }
                binding.pbProfileLoading.setVisibility(View.GONE);
                ToastUtils.show(getContext(), "File upload error: " + t.getMessage());
                Log.e(TAG, "File upload onFailure", t);
                if(finalImageFile.exists()) finalImageFile.delete();
            }
        });
    }

    private void linkImageToUserProfile(String profilePictureUrl, File tempImageFileToClean) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.USER_PREFS, Context.MODE_PRIVATE);
        String identifier = sharedPreferences.getString(LoginActivity.IDENTIFIER_KEY, null);

        if (TextUtils.isEmpty(identifier)) {
            ToastUtils.show(getContext(), "User identifier not found. Cannot update profile picture.");
             if (binding != null) binding.pbProfileLoading.setVisibility(View.GONE);
            if(tempImageFileToClean != null && tempImageFileToClean.exists()) tempImageFileToClean.delete();
            return;
        }
        if (binding == null) {
            if(tempImageFileToClean != null && tempImageFileToClean.exists()) tempImageFileToClean.delete();
            return;
        }

        Log.d(TAG, "Attempting to link image to profile. User: " + identifier + ", URL: " + profilePictureUrl);

        apiService.changeProfilePictureOnBackend(identifier, profilePictureUrl).enqueue(new Callback<APIResponse<StatusResponse>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<StatusResponse>> call, @NonNull Response<APIResponse<StatusResponse>> response) {
                if (binding == null || getContext() == null) {
                    if(tempImageFileToClean != null && tempImageFileToClean.exists()) tempImageFileToClean.delete();
                    return;
                }
                binding.pbProfileLoading.setVisibility(View.GONE);
                if (tempImageFileToClean != null && tempImageFileToClean.exists()) {
                    tempImageFileToClean.delete();
                }

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<StatusResponse> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                        StatusResponse innerStatusResponse = apiResponse.getData();
                        if ("SUCCESS".equalsIgnoreCase(innerStatusResponse.getStatus())) {
                            ToastUtils.show(getContext(), innerStatusResponse.getMessage());
                            Log.d(TAG, "Profile picture linked successfully on backend.");
                            // Update the currentUserProfile with the new image URL
                            if (currentUserProfile != null) {
                                currentUserProfile.setProfileImageUrl(profilePictureUrl);
                            }
                            if (getContext() != null) {
                               Glide.with(ProfileFragment.this)
                                     .load(profilePictureUrl)
                                     .circleCrop()
                                     .placeholder(android.R.drawable.sym_def_app_icon)
                                     .error(android.R.drawable.sym_def_app_icon)
                                     .into(binding.ivProfileAvatar);
                            }
                        } else {
                            ToastUtils.show(getContext(), "Failed to update profile: " + (innerStatusResponse.getMessage() != null ? innerStatusResponse.getMessage() : apiResponse.getMessage()));
                        }
                    } else {
                         ToastUtils.show(getContext(), "Failed to update profile picture (API Error): " + apiResponse.getMessage());
                    }
                } else {
                    ToastUtils.show(getContext(), "Failed to update profile picture on backend. Code: " + response.code());
                     try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Backend profile update error: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading backend profile update error body", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<StatusResponse>> call, @NonNull Throwable t) {
                if (binding == null || getContext() == null) {
                    if(tempImageFileToClean != null && tempImageFileToClean.exists()) tempImageFileToClean.delete();
                    return;
                }
                binding.pbProfileLoading.setVisibility(View.GONE);
                if (tempImageFileToClean != null && tempImageFileToClean.exists()) {
                    tempImageFileToClean.delete();
                }
                ToastUtils.show(getContext(), "Error updating profile picture: " + t.getMessage());
                Log.e(TAG, "Backend profile update onFailure", t);
            }
        });
    }

    private void removeProfilePhoto() {
        if (getContext() == null) return;
        // TODO: Implement backend call for removing profile photo
        // For now, just update UI and currentUserProfile
        ToastUtils.show(getContext(), "Profile photo removed (UI only - backend not fully implemented).");
        if(binding != null) {
            binding.ivProfileAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        }
        if (currentUserProfile != null) {
            currentUserProfile.setProfileImageUrl(null); // Or empty string, depending on your backend
        }
    }

    private void fetchUserProfile() {
        if (getContext() == null || apiService == null) {
            ToastUtils.show(getContext(), "Error: Profile services unavailable.");
            if (binding != null) {
                 binding.pbProfileLoading.setVisibility(View.GONE);
                 binding.profileContentContainer.setVisibility(View.VISIBLE);
                 setFieldsToNA();
            }
            return;
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.USER_PREFS, Context.MODE_PRIVATE);
        String identifier = sharedPreferences.getString(LoginActivity.IDENTIFIER_KEY, null);

        if (TextUtils.isEmpty(identifier)) {
            ToastUtils.show(getContext(), "Error: User identifier not found.");
            if (binding != null) {
                binding.pbProfileLoading.setVisibility(View.GONE);
                binding.profileContentContainer.setVisibility(View.VISIBLE);
                setFieldsToNA();
            }
            return;
        }
        if (binding == null) return;

        binding.pbProfileLoading.setVisibility(View.VISIBLE);
        binding.profileContentContainer.setVisibility(View.INVISIBLE);

        apiService.getUserProfile(identifier).enqueue(new Callback<APIResponse<UserProfile>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<UserProfile>> call, @NonNull Response<APIResponse<UserProfile>> response) {
                if (binding == null || getContext() == null) return;

                binding.pbProfileLoading.setVisibility(View.GONE);
                binding.profileContentContainer.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<UserProfile> apiResponse = response.body();
                    if (apiResponse.getData() != null && apiResponse.getStatus() == 200) {
                        currentUserProfile = apiResponse.getData(); // Store fetched profile
                        binding.tvProfileName.setText(TextUtils.isEmpty(currentUserProfile.getName()) ? "User Name" : currentUserProfile.getName());
                        binding.tvProfileEmail.setText(TextUtils.isEmpty(currentUserProfile.getEmail()) ? "No email" : currentUserProfile.getEmail());
                        binding.tvProfilePhone.setText("Phone: " + (TextUtils.isEmpty(currentUserProfile.getPhone()) ? "N/A" : currentUserProfile.getPhone()));

                        if (getContext() != null && !TextUtils.isEmpty(currentUserProfile.getProfileImageUrl())) {
                            Glide.with(ProfileFragment.this)
                                 .load(currentUserProfile.getProfileImageUrl())
                                 .circleCrop()
                                 .placeholder(android.R.drawable.sym_def_app_icon)
                                 .error(android.R.drawable.sym_def_app_icon)
                                 .into(binding.ivProfileAvatar);
                        } else {
                             if (getContext() != null) {
                                binding.ivProfileAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
                             }
                        }
                    } else {
                        ToastUtils.show(getContext(), "Failed to load profile: " + (apiResponse.getMessage() != null ? apiResponse.getMessage() : "Unknown error"));
                        setFieldsToNA();
                        currentUserProfile = null; // Clear if failed
                    }
                } else {
                    ToastUtils.show(getContext(), "Failed to load profile. Error code: " + response.code());
                    setFieldsToNA();
                    currentUserProfile = null; // Clear if failed
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<UserProfile>> call, @NonNull Throwable t) {
                 if (binding == null || getContext() == null) return;

                binding.pbProfileLoading.setVisibility(View.GONE);
                binding.profileContentContainer.setVisibility(View.VISIBLE);
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                ToastUtils.show(getContext(), "Error loading profile: " + t.getMessage());
                setFieldsToNA();
                currentUserProfile = null; // Clear if failed
            }
        });
    }

    private void setFieldsToNA() {
        if (binding == null) return;
        binding.tvProfileName.setText("User Name");
        binding.tvProfileEmail.setText("No email");
        binding.tvProfilePhone.setText("Phone: N/A");
        if (getContext() != null) {
            binding.ivProfileAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
