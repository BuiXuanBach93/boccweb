package jp.bo.bocc.helper;

/**
 * Created by buixu on 11/20/2017.
 */
public class VersionUtils implements Comparable<VersionUtils> {

    private String version;

    public final String get() {
        return this.version;
    }

    public VersionUtils(String version) {
        if(version == null)
            version = "0";
        if(!version.matches("[0-9]+(\\.[0-9]+)*"))
            version = "0";
        this.version = version;
    }

    @Override public int compareTo(VersionUtils that) {
        if(that == null)
            return 1;
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override public boolean equals(Object that) {
        if(this == that)
            return true;
        if(that == null)
            return false;
        if(this.getClass() != that.getClass())
            return false;
        return this.compareTo((VersionUtils) that) == 0;
    }

}
