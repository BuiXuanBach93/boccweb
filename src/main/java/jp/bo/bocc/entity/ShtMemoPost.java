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
 * @author NguyenThuong on 4/20/2017.
 */
@Entity
@Table(name = "SHT_MEMO_POST")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
public class ShtMemoPost extends BaseEntity {
    @Id
    @Column(name = "MEMO_POST_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_MEMO_POST_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private ShmAdmin admin;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private ShmPost post;

    @Getter @Setter
    @Column(name = "MEMO_CONTENT")
    private String content;

    @Getter @Setter
    @Transient
    private int sequentNumber;

    @Setter @Getter
    @Transient
    private String shmAdminName;
}
