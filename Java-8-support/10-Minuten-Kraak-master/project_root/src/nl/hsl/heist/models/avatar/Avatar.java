package nl.hsl.heist.models.avatar;

import java.io.Serializable;
/**
 * Parent class for all Avatars
 *
 * @author Jordi,Joorden
 */
public abstract class Avatar implements Serializable {

    final static long serialVersionUID = 23908593478L;

    private String imgPath;
    private String miniImgPath;

    public Avatar(String imgPath, String miniImgPath) {
        this.imgPath = imgPath;
        this.miniImgPath= miniImgPath;
    }
    public String getImgPath() {
        return imgPath;
    }
    public String getMiniImgPath(){return miniImgPath;}
}
