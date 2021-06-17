package brs.http.api;

import brs.util.Convert;
import burst.kit.entity.BurstAddress;
import lombok.Getter;

@Getter
public class AccountDto {
	private String id;
	private String idRS;
	
	public AccountDto(Long id) {
	  this.id = Convert.toUnsignedLong(id);
	  this.idRS = BurstAddress.fromId(id).getFullAddress();
	}
}
