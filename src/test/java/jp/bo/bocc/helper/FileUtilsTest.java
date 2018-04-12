package jp.bo.bocc.helper;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShrImage;
import jp.bo.bocc.enums.ImageSavedEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Namlong on 3/31/2017.
 */
public class FileUtilsTest {
    private String str = "[{\"org\":1,\"thb\":2},{\"org\":10,\"thb\":12}]";

    @Test
    public void buildImageJson() throws Exception {
        ShrFile shr = new ShrFile();
        shr.setId(1L);
        ShrFile shr1 = new ShrFile();
        shr1.setId(2L);

        ShrFile shr2 = new ShrFile();
        shr2.setId(10L);
        ShrFile shr3 = new ShrFile();
        shr3.setId(12L);

        List<ShrImage> list = new ArrayList<>();
        ShrImage s = new ShrImage(shr, shr1);
        ShrImage s1 = new ShrImage(shr2, shr3);
        list.add(s);
        list.add(s1);
        Assert.assertEquals(str, FileUtils.buildImageJson(list));

    }

    @Test
    public void getImageId() throws Exception {
        final Long imageId = FileUtils.getImageId(str, 0, ImageSavedEnum.ORIGINAL);
        Assert.assertEquals(Long.valueOf(1), imageId);

    }

    @Test
    public void getAllImageIdByType() throws Exception {
        final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(str, ImageSavedEnum.ORIGINAL);
        Assert.assertEquals(Long.valueOf(1), allImageIdByType.get(0));
        Assert.assertEquals(Long.valueOf(10), allImageIdByType.get(1));
    }

}