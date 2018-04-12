package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;


/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHT_ADMIN_CSV_HST")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
public class ShtAdminCsvHst extends BaseEntity {

    @Id
    @Column(name = "ADMIN_CSV_HST_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_ADMIN_CSV_HST_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter @Setter
    private Long adminCsvHstId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin admin;

    @Column(name = "ADMIN_CSV_HST_TYPE")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private CSV_TYPE adminCsvHstType;

    public enum CSV_TYPE {
        USER,
        POST,
        USER_PATROL,
        POST_PATROL,
        TORINO_CSV
    }

    @Getter @Setter
    @Transient
    private int maxRecord;
}
