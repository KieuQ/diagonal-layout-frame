package idiocy.com.frame;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    View ivt, ivb, ivl, ivr, ivl2, ivr2, ivlp, ivrp, ivl2p, ivr2p;
    SeekBar sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivt = findViewById(R.id.ivt);
        ivb = findViewById(R.id.ivb);
        ivl = findViewById(R.id.ivl);
        ivr = findViewById(R.id.ivr);
        ivl2 = findViewById(R.id.ivl2);
        ivr2 = findViewById(R.id.ivr2);
        ivlp = findViewById(R.id.ivlp);
        ivrp = findViewById(R.id.ivrp);
        ivl2p = findViewById(R.id.ivl2p);
        ivr2p = findViewById(R.id.ivr2p);

        sb = findViewById(R.id.sb);
        sb.setProgress(50);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setFrameImages(seekBar.getProgress());
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        setFrameImages(50);
    }

    public void setFrameImages(final int w) {
        new AsyncTask<Void, Void, BitmapDrawable[]>() {
            @Override
            protected BitmapDrawable[] doInBackground(Void... voids) {
                ivb.getLayoutParams().height = w;
                ivlp.getLayoutParams().width = w;
                ivrp.getLayoutParams().width = w;
                ivt.getLayoutParams().height = w;
                ivl2p.getLayoutParams().width = w;
                ivr2p.getLayoutParams().width = w;

                Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.frame)).getBitmap();

                Bitmap bmpt, bmpb, bmpl, bmpr;
                bmpl = scaleToFitWidth(bmp, w);
                bmpr = rotateBitmap(bmpl, ExifInterface.ORIENTATION_ROTATE_180);
                bmpt = rotateBitmap(bmpl, ExifInterface.ORIENTATION_ROTATE_90);
                bmpb = rotateBitmap(bmpr, ExifInterface.ORIENTATION_ROTATE_90);

                BitmapDrawable bmpdt, bmpdb, bmpdl, bmpdr;
                bmpdl = new BitmapDrawable(getResources(), bmpl);
                bmpdr = new BitmapDrawable(getResources(), bmpr);
                bmpdt = new BitmapDrawable(getResources(), bmpt);
                bmpdb = new BitmapDrawable(getResources(), bmpb);

                bmpdl.setTileModeY(Shader.TileMode.REPEAT);
                bmpdr.setTileModeY(Shader.TileMode.REPEAT);
                bmpdt.setTileModeX(Shader.TileMode.REPEAT);
                bmpdb.setTileModeX(Shader.TileMode.REPEAT);

                BitmapDrawable[] bds = {bmpdl, bmpdr, bmpdt, bmpdb};
                return bds;
            }

            @Override
            protected void onPostExecute(BitmapDrawable[] bds) {
                setViewBackground(ivl, bds[0]);
                setViewBackground(ivr, bds[1]);
                setViewBackground(ivl2, bds[0]);
                setViewBackground(ivr2, bds[1]);
                setViewBackground(ivt, bds[2]);
                setViewBackground(ivb, bds[3]);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void setViewBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }
    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        if(b == null) {
            return null;
        }
        width = width < 1 ? 1 : width;
        float factor = width / (float) b.getWidth();
        int height = (int) (b.getHeight() * factor);
        height = height < 1 ? 1 : height;
        return Bitmap.createScaledBitmap(b, width, height, true);
    }

    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        if(b == null) {
            return null;
        }
        height = height < 1 ? 1 : height;
        float factor = height / (float) b.getHeight();
        int width = (int) (b.getWidth() * factor);
        width = width < 1 ? 1 : width;
        return Bitmap.createScaledBitmap(b, width, height, true);
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        bitmap = bitmap.copy(bitmap.getConfig(), true);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
