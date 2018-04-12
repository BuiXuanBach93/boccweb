package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * @author manhnt
 */

@Entity
@Table(name = "SHM_ADDR")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShmAddr extends BaseEntity {

	@Id
	@Column(name = "ADDR_ID")
	@SequenceGenerator(name = "seq", sequenceName = "SHM_ADDR_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	@Getter @Setter
	private Long addressId;

	@Column(name = "ADDR_AREA_CODE", length = 10)
	@Getter @Setter
	private String areaCode;

	@Column(name = "ADDR_AREA_NAME", length = 100)
	@Getter @Setter
	private String areaName;

	@Column(name="ADDR_PARENT_ID")
	@Getter @Setter
	private Long addrParentId;

	@Transient
	@Getter @Setter
	private String fullAreaName;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ShmAddr address = (ShmAddr) o;

		if (!addressId.equals(address.addressId)) return false;
		if (!areaCode.equals(address.areaCode)) return false;
		if (!areaName.equals(address.areaName)) return false;
		if (!addrParentId.equals(address.addrParentId)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = addressId.hashCode();
		result = 31 * result + areaCode.hashCode();
		result = 31 * result + areaName.hashCode();
		result = 31 * result + addrParentId.hashCode();
		return result;
	}
}
