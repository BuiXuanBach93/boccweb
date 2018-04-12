package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Created by buixu on 11/14/2017.
 */
@Entity
@Table(name = "SHT_GROUP_PBL_DETAIL")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_GROUP_PBL_DETAIL\" SET CMN_DELETE_FLAG = 1 WHERE GROUP_DETAIL_ID = ?")
public class ShtGroupPblDetail extends BaseEntity {
    @Id
    @Column(name = "GROUP_DETAIL_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_GROUP_PBL_DETAIL_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long groupDetailId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GROUP_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShtGroupPublish groupPublish;

    @Column(name = "LEGAL_ID")
    @Getter @Setter
    private String legalId;

}
