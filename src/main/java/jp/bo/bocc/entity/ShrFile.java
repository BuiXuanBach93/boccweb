package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
/**
 * @author manhnt
 */
@Entity
@Table(name = "SHR_FILE")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
@Where(clause = "CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHR_FILE\" SET CMN_DELETE_FLAG = 1 WHERE FILE_ID = ?")
public class ShrFile extends BaseEntity {

    @Id
    @Column(name = "FILE_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHR_FILE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "FILE_PROVIDER", length = 100)
    @Getter @Setter
    private String provider = "local";

    @Column(name = "FILE_ORG_NAME", length = 100)
    @Getter @Setter
    private String originalName;

    @Column(name = "FILE_NAME", length = 100)
    @Getter @Setter
    private String name;

    @Column(name = "FILE_WIDTH")
    @Getter @Setter
    private Integer width;

    @Column(name = "FILE_HEIGHT")
    @Getter @Setter
    private Integer height;

    @Column(name = "FILE_SIZE")
    @Getter @Setter
    private Long size;

    @Column(name = "FILE_EXT", length = 10)
    @Getter @Setter
    private String extension;

    @Column(name = "FILE_DIR", length = 1000)
    @Getter @Setter
    private String dir;

    public void setDir(String dir) {
        if (dir == null || !dir.endsWith("/")) {
            throw new IllegalArgumentException("Directory name must be end with '/' character");
        }
        this.dir = dir;
    }

    @Transient
    public String getPath() {
        return getDir() + getName() + "." + getExtension();
    }
}