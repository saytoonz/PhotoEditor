package com.example.say.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.say.myapplication.Interfaces.AddFrameListener;
import com.example.say.myapplication.Interfaces.AddTextFragmentListner;
import com.example.say.myapplication.Interfaces.BrushFragmentListener;
import com.example.say.myapplication.Interfaces.EditImageFragmentListner;
import com.example.say.myapplication.Interfaces.EmojiFragmentListener;
import com.example.say.myapplication.Interfaces.FiltersListFragmentListener;
import com.example.say.myapplication.Utils.BitmapUtils;
import com.example.say.myapplication.adapter.ViewPagerAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListner, BrushFragmentListener, EmojiFragmentListener, AddTextFragmentListner, AddFrameListener {
    public final static String pictureName = "img.jpg";
    public final static int PERMISSION_PICK_IMAGE = 100;
    public final static int PERMISSION_INSERT_IMAGE = 1001;
    private static final int CAMERA_REQUEST = 1002;

    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;
    CardView but_filter_list, but_edit, but_brush, but_emoji, but_add_text, but_add_image, but_add_frame, but_crop;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float constrantFinal = 1.0f;

    Uri image_selecter_uri;

    //Load native image filters lib
    static {
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.filterToolbar1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("UENR FACE");


        //View
        photoEditorView = (PhotoEditorView) findViewById(R.id.filterImage_preview);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "emojione-android.ttf"))
                .build();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.filter_coordinatorLayout);

        but_edit = (CardView) findViewById(R.id.but_edit_image);
        but_filter_list = (CardView) findViewById(R.id.but_filter_list);
        but_brush = (CardView) findViewById(R.id.but_brush);
        but_emoji = (CardView) findViewById(R.id.but_emoji);
        but_add_text = (CardView) findViewById(R.id.but_add_text);
        but_add_image = (CardView) findViewById(R.id.but_add_image);
        but_add_frame = (CardView) findViewById(R.id.but_add_frame);
        but_crop = (CardView) findViewById(R.id.but_crop);


        but_filter_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filtersListFragment != null) {

                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());

                } else {

                    FiltersListFragment filtersListFragment = FiltersListFragment.getInstance(null);
                    filtersListFragment.setListener(MainActivity.this);
                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());
                }
            }
        });

        but_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListner(MainActivity.this);
                editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());
            }
        });

        but_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Enable brush as default
                photoEditor.setBrushDrawingMode(true);

                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(MainActivity.this);
                brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());
            }
        });

        but_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmojiFragment emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListener(MainActivity.this);
                emojiFragment.show(getSupportFragmentManager(), emojiFragment.getTag());
            }
        });

        but_add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTextFragment addTextFragment = AddTextFragment.getInstance();
                addTextFragment.setListner(MainActivity.this);
                addTextFragment.show(getSupportFragmentManager(), addTextFragment.getTag());
            }
        });

        but_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageToPhoto();
            }
        });

        but_add_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameFragment frameFragment = FrameFragment.getInstance();
                frameFragment.setListener(MainActivity.this);
                frameFragment.show(getSupportFragmentManager(), frameFragment.getTag());
            }
        });

        but_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop(image_selecter_uri);
            }
        });

        loadImage();


    }


    private void startCrop(Uri uri) {
        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop.start(MainActivity.this);

    }


    private void addImageToPhoto() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_INSERT_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    private void loadImage() {
        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(originalBitmap);

    }


    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListner(this);

        adapter.addFragment(filtersListFragment, "FILTERS");
        adapter.addFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(adapter);

    }


    @Override
    public void onBrightnessChanged(int Brightness) {
        brightnessFinal = Brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(Brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }


    @Override
    public void onSaturationChanged(float Saturation) {
        saturationFinal = Saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(Saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }


    @Override
    public void onContrastChanged(float Contrast) {
        constrantFinal = Contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(Contrast));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }


    @Override
    public void onEditStarted() {

    }


    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constrantFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        finalBitmap = myFilter.processFilter(bitmap);
    }


    @Override
    public void onFilterSelecter(Filter filter) {
        //resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControl() {
        if (editImageFragment != null)
            editImageFragment.resetControls();
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        constrantFinal = 1.0f;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_action_camera) {
            opemCamera();
            return true;
        } else if (id == R.id.filter_action_open) {
            openImageFromGallery();
            return true;
        } else if (id == R.id.filter_action_save) {
            saveImageToGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void opemCamera() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {


                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE,"UENR Face");
                            values.put(MediaStore.Images.Media.DESCRIPTION,"Cam Pic");
                            image_selecter_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_selecter_uri);
                            startActivityForResult(cameraIntent,CAMERA_REQUEST);

                        } else {
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }


    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {


                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {


                                    try {
                                        photoEditorView.getSource().setImageBitmap(saveBitmap);

                                        final String path = BitmapUtils.insertImage(
                                                getContentResolver(),
                                                saveBitmap,
                                                System.currentTimeMillis() + "_uenrface",
                                                null);

                                        if (!TextUtils.isEmpty(path)) {

                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Image saved to gallery",
                                                    Snackbar.LENGTH_LONG)
                                                    .setAction("OPEN", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            openImage(path);
                                                        }
                                                    });

                                            snackbar.show();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Image unable to save",
                                                    Snackbar.LENGTH_LONG);

                                            snackbar.show();
                                        }


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }


    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        } else {
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_REQUEST) {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, image_selecter_uri, 800, 800);

                //clear bitmap memory
                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);

            } else if (requestCode == PERMISSION_PICK_IMAGE) {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

                image_selecter_uri = data.getData();

                //clear bitmap memory
                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);

            } else if (requestCode == PERMISSION_INSERT_IMAGE) {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 250, 250);
                photoEditor.addImage(bitmap);
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);

            }


        } else if (requestCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private void handleCropError(Intent data) {
        final Throwable cropErorr = UCrop.getError(data);
        if (cropErorr != null) {
            Toast.makeText(this, "" + cropErorr.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unexpected Error...", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);

        if (resultUri != null){

            photoEditorView.getSource().setImageURI(resultUri);

            Bitmap bitmap = ((BitmapDrawable)photoEditorView.getSource().getDrawable()).getBitmap();
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        } else {
            Toast.makeText(this, "Cannot retrieve crop image...", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {
        if (isEraser)
            photoEditor.brushEraser();
        else
            photoEditor.setBrushDrawingMode(true);
    }

    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
    }


    @Override
    public void onAddTextButtonClicked(Typeface typeface, String text, int color) {
        photoEditor.addText(typeface, text, color);
    }

    @Override
    public void onAddFrame(int frame) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), frame);
        photoEditor.addImage(bitmap);
    }
}
