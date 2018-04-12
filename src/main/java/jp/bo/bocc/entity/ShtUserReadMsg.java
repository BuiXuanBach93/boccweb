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
 * Created by buixu on 11/14/2017.
 */
@Entity
@Table(name = "SHT_USER_READ_MSG")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_USER_READ_MSG\" SET CMN_DELETE_FLAG = 1 WHERE READ_MSG_ID = ?")
public class ShtUserReadMsg extends BaseEntity {
    @Id
    @Column(name = "READ_MSG_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_USER_READ_MSG_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long readMsgId;

    @Column(name = "QA_ID")
    @Getter @Setter
    private Long qaId;

    @Column(name = "USER_ID")
    @Getter @Setter
    private Long userId;
}
