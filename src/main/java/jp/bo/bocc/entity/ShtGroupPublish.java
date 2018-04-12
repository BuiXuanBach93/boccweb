package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;


/**
 * Created by buixu on 11/14/2017.
 */
@Entity
@Table(name = "SHT_GROUP_PUBLISH")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_GROUP_PUBLISH\" SET CMN_DELETE_FLAG = 1 WHERE GROUP_ID = ?")
public class ShtGroupPublish extends BaseEntity {
    @Id
    @Column(name = "GROUP_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_GROUP_PUBLISH_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long groupId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin shmAdmin;

    @Column(name = "GROUP_NO")
    @Getter @Setter
    private String groupNo;

    @Column(name = "GROUP_NAME")
    @Getter @Setter
    private String groupName;

    @Getter @Setter
    @Column(name = "LEGAL_NUMBER")
    private Long legalNumber;

    @Transient
    @Getter @Setter
    private List<ShtGroupPblDetail> groupPblDetails;
}
