package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by buixu on 12/19/2017.
 */
@Entity
@Table(name = "SHT_KPI_STORAGE")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_KPI_STORAGE\" SET CMN_DELETE_FLAG = 1 WHERE KPI_ID = ?")
public class ShtKpiStorage extends BaseEntity {
    @Id
    @Column(name = "KPI_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_KPI_STORAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long kpiId;

    @Column(name = "KPI_QUERY_TIME")
    @Getter @Setter
    private String queryTime;

    @Column(name = "KPI_TYPE")
    @Getter @Setter
    private KpiTypeEmum kpiType;


    @Column(name = "DL_NUMBER")
    @Getter @Setter
    private Long dlNumber;

    @Column(name = "REG_NUMBER")
    @Getter @Setter
    private Long regNumber;

    @Column(name = "REG_RATIO")
    @Getter @Setter
    private Double regRatio;

    @Column(name = "OWNER_NUMBER")
    @Getter @Setter
    private Long ownerNumber;

    @Column(name = "POST_NUMBER")
    @Getter @Setter
    private Long postNumber;

    @Column(name = "POST_RATIO")
    @Getter @Setter
    private Double postRatio;

    @Column(name = "PARTNER_NUMBER")
    @Getter @Setter
    private Long partnerNumber;

    @Column(name = "TRANS_NUMBER")
    @Getter @Setter
    private Long transNumber;

    @Column(name = "TRANS_RATIO")
    @Getter @Setter
    private Double transRatio;

    @Getter @Setter
    @Column(name = "ACTOR_NUMBER")
    private Long actorNumber;


    public enum KpiTypeEmum {
        DAILY, MONTHLY
    }

}
