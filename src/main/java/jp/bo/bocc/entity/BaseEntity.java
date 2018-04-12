package jp.bo.bocc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jp.bo.bocc.system.config.Log;
import jp.bo.bocc.system.config.audit.Auditor;
import jp.bo.bocc.system.exception.InternalServerErrorException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * @author manhnt
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
class BaseEntity implements java.io.Serializable{

	@Column(name = "CMN_DELETE_FLAG")
	@Getter @Setter
	private Boolean deleteFlag = false;

	@Column(name = "CMN_ENTRY_DATE", updatable = false)
	@Getter @Setter
	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "CMN_LAST_UPDT_DATE")
	@Getter @Setter
	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Column(name = "CMN_ENTRY_USER_NO", updatable = false)
	@Getter @Setter
	@JsonIgnore
	@CreatedBy
	@Convert(converter = AuditorInfoConverter.class)
	private Auditor createdBy;

	@Column(name = "CMN_ENTRY_USER_TYPE", updatable = false)
	@Enumerated
	@Getter @Setter
	@JsonIgnore
	private Auditor.UserType createdByType;

	@Column(name = "CMN_LAST_UPDT_USER_NO")
	@Getter @Setter
	@JsonIgnore
	@LastModifiedBy
	@Convert(converter = AuditorInfoConverter.class)
	private Auditor updatedBy;

	@Column(name = "CMN_LAST_UPDT_USER_TYPE")
	@Enumerated
	@Getter @Setter
	@JsonIgnore
	private Auditor.UserType updatedByType;

	@PrePersist
	public void onPrePersist() {
		this.createdByType = createdBy.getUserType();
		this.updatedByType = updatedBy.getUserType();
	}

	@PreUpdate
	public void onPreUpdate() {
		this.updatedByType = updatedBy.getUserType();
	}

	@PostLoad
	public void postLoad() {
		this.createdBy.setUserType(this.createdByType);
		this.updatedBy.setUserType(this.updatedByType);
	}

	public static class AuditorInfoConverter implements AttributeConverter<Auditor, Long> {
		@Override
		public Long convertToDatabaseColumn(Auditor auditor) {
			return auditor.getUserId();
		}

		@Override
		public Auditor convertToEntityAttribute(Long userId) {
			Auditor auditor = new Auditor();
			auditor.setUserId(userId);
			return auditor;
		}
	}

	/**
	 * Merge this entity's properties with newObj's properties. Ignore null value
	 * @param newObj
	 */
	public void merge(Object newObj) {
		//Not the same type OR not an entity
		if (!this.getClass().equals(newObj.getClass()) || !this.getClass().isAnnotationPresent(Entity.class)) return;
		try {
			copy(this, newObj, this.getClass());
		} catch (IllegalAccessException e) {
			throw new InternalServerErrorException("Error while updating entity's properties. Entity name: " + this.getClass().getSimpleName());
		}
	}

	private static void copy(Object oldObj, Object newObj, Class<?> clazz) throws IllegalAccessException {
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(JoinColumn.class)) {
				field.setAccessible(true);
				Object fieldValue = field.get(newObj);
				if (fieldValue != null) { // Set field to old object
					field.set(oldObj, fieldValue);
					Log.REPOSITORY_LOG.trace(String.format("[GET/SET][%s][%s]", clazz.getSimpleName(), field.getName()));
				}
			}
		}

		//copy super class
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null ) {
			copy(oldObj, newObj, superclass);
		}
	}
}
