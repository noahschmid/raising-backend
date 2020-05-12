package ch.raising.models.responses;

import java.sql.Timestamp;

public class IOSSubscription {
	private final int status;
	private final Timestamp expiresDate;
	private final String latestReceiptData;
	private final String originalTransactionId;

	private IOSSubscription(int status, Timestamp expiresDate, String latestReceiptData,
			String originalTransactionId) {
		this.status = status;
		this.expiresDate = expiresDate;
		this.latestReceiptData = latestReceiptData;
		this.originalTransactionId = originalTransactionId;
	}

	public int getStatus() {
		return status;
	}

	public Timestamp getExpiresDate() {
		return expiresDate;
	}

	public String getLatestReceiptData() {
		return latestReceiptData;
	}

	public String getOriginalTransactionId() {
		return originalTransactionId;
	}
	
	@Override
	public String toString() {
		return "{status: " + status + ", expiresDate: " + expiresDate + ", latestReceiptData: " + latestReceiptData
				+ ", originalTransactionId: " + originalTransactionId +"}";
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private int status;
		private Timestamp expiresDate;
		private String latestReceiptData;
		private String originalTransactionId;

		public Builder status(int status) {
			this.status = status;
			return this;
		}

		public Builder expiresDate(long expiresDate) {
			this.expiresDate = new Timestamp(expiresDate);
			return this;
		}

		public Builder latestReceiptData(String latestReceiptData) {
			this.latestReceiptData = latestReceiptData;
			return this;
		}

		public Builder originalTransactionId(String originalTransactionId) {
			this.originalTransactionId = originalTransactionId;
			return this;
		}

		public IOSSubscription build() {
			return new IOSSubscription(status, expiresDate, latestReceiptData, originalTransactionId);
		}
	}

}
