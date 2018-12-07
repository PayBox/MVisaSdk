package money.paybox.pbsdkmvisa.utils;

/**
 * Created by am on 14.05.2018.
 */

public interface CameraListener {
    void createCamera();
    void showDialog();
    boolean onTap(float rawX, float rawY);
    void doZoom(float scale);
}
