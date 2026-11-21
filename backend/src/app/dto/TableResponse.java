package app.dto;

import java.math.BigDecimal;

import app.db.entity.BarTable;
import app.db.entity.TableType;

public class TableResponse {
	public Long id;
	public Long barId;
	public String label;
	public TableType tableType;
	public Integer capacity;
	public BigDecimal rate;
	public Boolean active;

	public static TableResponse from(BarTable t) {
		TableResponse r = new TableResponse();
		r.id = t.getId();
		r.barId = t.getBar().getId();
		r.label = t.getLabel();
		r.tableType = t.getTableType();
		r.capacity = t.getCapacity();
		r.rate = Formats.plain(t.getRate());
		r.active = t.getActive();
		return r;
	}
}
