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
@Table(name = "SHT_MEMO_QA")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
public class ShtMemoQa extends BaseEntity {

    @Id
    @Column(name = "MEMO_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_MEMO_QA_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter @Setter
    private Long memoId;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QA_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private ShtQa qa;

    @Getter @Setter
    @Column(name = "MEMO_CONT")
    private String content;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private ShmAdmin admin;
}
