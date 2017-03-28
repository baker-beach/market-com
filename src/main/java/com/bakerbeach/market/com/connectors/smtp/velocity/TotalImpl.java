package com.bakerbeach.market.com.connectors.smtp.velocity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.core.api.model.Total;

public class TotalImpl implements Total, Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String qualifier;
	protected BigDecimal gross = BigDecimal.ZERO;
	protected BigDecimal net = BigDecimal.ZERO;
	protected BigDecimal quantity = BigDecimal.ZERO;
	protected Map<TaxCode, Line> lines = new HashMap<TaxCode, Line>(2);

	public void clear() {
		this.gross = BigDecimal.ZERO;
		this.net = BigDecimal.ZERO;
		this.quantity = BigDecimal.ZERO;
		this.lines.clear();
	}

	public void addGross(BigDecimal summand) {
		gross = gross.add(summand);
	}

	public void addQuantity(BigDecimal summand) {
		quantity = quantity.add(summand);
	}

	@Override
	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	public BigDecimal getGross() {
		return gross;
	}

	public void setGross(BigDecimal gross) {
		this.gross = gross;
	}

	@Override
	public BigDecimal getNet() {
		return net;
	}

	public void setNet(BigDecimal net) {
		this.net = net;
	}

	@Override
	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	@Override
	public Map<TaxCode, Line> getLines() {
		return lines;
	}

	public void setLines(Map<TaxCode, Line> lines) {
		this.lines = lines;
	}

	public static class LineImpl implements Line, Serializable {

		private static final long serialVersionUID = 1L;

		protected TaxCode taxCode;
		protected BigDecimal taxPercent;
		protected BigDecimal gross = BigDecimal.ZERO;
		protected BigDecimal net = BigDecimal.ZERO;
		protected BigDecimal tax = BigDecimal.ZERO;
		protected BigDecimal quantity = BigDecimal.ZERO;

		public LineImpl(TaxCode taxCode, BigDecimal taxPercent) {
			this.taxCode = taxCode;
			this.taxPercent = taxPercent;
		}

		@Override
		public void addGross(BigDecimal summand) {
			gross = gross.add(summand);
		}

		@Override
		public void addQuantity(BigDecimal summand) {
			quantity = quantity.add(summand);
		}

		@Override
		public BigDecimal getGross() {
			return gross;
		}

		@Override
		public void setGross(BigDecimal gross) {
			this.gross = gross;
		}

		@Override
		public BigDecimal getNet() {
			return net;
		}

		@Override
		public void setNet(BigDecimal net) {
			this.net = net;
		}

		@Override
		public BigDecimal getTax() {
			return tax;
		}

		@Override
		public void setTax(BigDecimal tax) {
			this.tax = tax;
		}

		@Override
		public TaxCode getTaxCode() {
			return taxCode;
		}
		
		@Override
		public void setTaxCode(TaxCode taxCode) {
			this.taxCode = taxCode;
		}

		@Override
		public BigDecimal getTaxPercent() {
			return taxPercent;
		}

		@Override
		public void setTaxPercent(BigDecimal taxPercent) {
			this.taxPercent = taxPercent;
		}

		// public BigDecimal getNet() {
		// return net;
		// }
		//
		// public void setNet(BigDecimal net) {
		// this.net = net;
		// }

		@Override
		public BigDecimal getQuantity() {
			return quantity;
		}

		@Override
		public void setQuantity(BigDecimal quantity) {
			this.quantity = quantity;
		}

		@Override
		public int compareTo(Line o) {
			return this.getGross().compareTo(o.getGross());
			// return o.getGross().compareTo(this.getGross());
		}

	}

}
