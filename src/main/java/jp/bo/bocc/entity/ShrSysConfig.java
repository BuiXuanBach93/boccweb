package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by Namlong on 6/30/2017.
 */
@Entity
@Table(name = "SHR_SYS_CONFIG")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShrSysConfig {
    @Id
    @Column(name = "SYS_CONFIG_CODE")
    private String sysConfigCode;

    @Column(name = "SYS_CONFIG_VALUE")
    @Getter @Setter
    private int sysConfigValue;

    @Column(name = "SYS_CONFIG_MSG")
    @Getter @Setter
    private String sysConfigMsg;

    @Column(name = "SYS_CONFIG_VALUES")
    @Getter @Setter
    private String sysConfigValues;
}
