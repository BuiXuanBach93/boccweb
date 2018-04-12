package jp.bo.bocc.entity;

import jp.bo.bocc.enums.AdminActionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHM_ADMIN_NG")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
public class ShmAdminNg extends BaseEntity {

    @Id
    @Column(name = "ADMIN_NG_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHM_ADMIN_NG_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter @Setter
    private Long adminNgId;

    @Getter @Setter
    @Column(name = "ADMIN_NG_CONTENT")
    private String adminNgContent;

    @Transient
    @Getter @Setter
    private Date formatEntryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID")
    @Setter
    @Getter
    @NotFound(action = NotFoundAction.IGNORE)
    private ShmAdmin shmAdmin;

    @Column(name = "SHM_ADMIN_ACTION")
    @Setter @Getter
    @Enumerated(EnumType.ORDINAL)
    private AdminActionEnum shmAdminAction;

}
