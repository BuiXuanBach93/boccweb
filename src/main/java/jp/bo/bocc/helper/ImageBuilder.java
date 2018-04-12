package jp.bo.bocc.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DonBach on 3/21/2017.
 */
public class ImageBuilder {
    private String name;
    private String dateFormat;
    private int thumbWidth;
    private int thumbHeight;
    private String imageFormat;
    private String rootDir;
    private String thumbDir;
    private String imgImportTempDir;

    public String getThumbDir() {
        return thumbDir;
    }

    public void setThumbDir(String thumbDir) {
        this.thumbDir = thumbDir;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getImgImportTempDir(){return this.imgImportTempDir;}

    public void setImgImportTempDir(String imgImportTempDir){this.imgImportTempDir = imgImportTempDir;}

    public String buildPostImageDir(long postId){
        DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
        Date date = new Date();
        String currentDateStr = dateFormat.format(date);
        String dirs = "posts/" + postId + "/" +  currentDateStr + "/" ;
        return dirs;
    }

    public String buildBannerImageDir(long bannerId){
        DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
        Date date = new Date();
        String currentDateStr = dateFormat.format(date);
        String dirs = "banners/" + bannerId + "/" +  currentDateStr + "/" ;
        return dirs;
    }

    public String buildBannerPageImageDir(long pageId){
        DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
        Date date = new Date();
        String currentDateStr = dateFormat.format(date);
        String dirs = "bannerpages/" + pageId + "/" +  currentDateStr + "/" ;
        return dirs;
    }

    public String buildTalkPurcImageDir(long talkPurcId){
        DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
        Date date = new Date();
        String currentDateStr = dateFormat.format(date);
        String dirs = "talkPurc/" + talkPurcId + "/" +  currentDateStr + "/" ;
        return dirs;
    }
}
