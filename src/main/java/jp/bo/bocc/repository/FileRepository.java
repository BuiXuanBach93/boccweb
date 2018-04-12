package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShrFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author manhnt
 */
public interface FileRepository extends JpaRepository<ShrFile, Long> {

	ShrFile findByDirAndNameAndExtension(String dir, String fileName, String extension);

	ShrFile findOne(Long id);

}
