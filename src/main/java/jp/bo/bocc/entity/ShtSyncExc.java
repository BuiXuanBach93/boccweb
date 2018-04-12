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
import java.time.LocalDateTime;


/**
 * Created by buixu on 03/06/2018.
 */
@Entity
@Table(name = "SHT_SYNC_EXC")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_SYNC_EXC\" SET CMN_DELETE_FLAG = 1 WHERE SYNC_EXC_ID = ?")
public class ShtSyncExc extends BaseEntity {
    @Id
    @Column(name = "SYNC_EXC_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_SYNC_EXC_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long syncExcId;

    @Column(name = "USER_NUMBER")
    @Getter @Setter
    private Long userNumber;

    @Column(name = "LEFT_DATE")
    @Getter
    @Setter
    private LocalDateTime leftDate;

    @Column(name = "TEND_TO_LEAVE_DATE")
    @Getter
    @Setter
    private LocalDateTime tendToLeaveDate;

    @Column(name = "QUERY_FROM_DATE")
    @Getter
    @Setter
    private LocalDateTime queryFromDate;

    @Column(name = "QUERY_TO_DATE")
    @Getter
    @Setter
    private LocalDateTime queryToDate;

    @Column(name = "STATUS", nullable = false)
    @Getter @Setter
    @Enumerated
    private SyncStatus status;

    public enum SyncStatus {
        START, DONE
    }

}
