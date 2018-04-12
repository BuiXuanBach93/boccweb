package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author manhnt
 */

@AllArgsConstructor @Getter @Setter
@NoArgsConstructor
public class ShrImage {

	private ShrFile original;

	private ShrFile thumbnail;
}
