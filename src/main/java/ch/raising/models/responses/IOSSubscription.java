package ch.raising.models.responses;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class IOSSubscription {
	private final int status;
	private final Timestamp expiresDate;
	private final String latestReceiptData;
	private final String originalTransactionId;
	private final String subscriptionId;

	private IOSSubscription(int status, Timestamp expiresDate, String latestReceiptData,
			String originalTransactionId, String subscriptionId) {
		this.status = status;
		this.expiresDate = expiresDate;
		this.latestReceiptData = latestReceiptData;
		this.originalTransactionId = originalTransactionId;
		this.subscriptionId = subscriptionId;
	}

	@JsonIgnore
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
	
	public String getSubscriptionId() {
		return subscriptionId;
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
		private String subscriptionId;

		public Builder status(int status) {
			this.status = status;
			return this;
		}

		public Builder expiresDate(long expiresDate) {
			this.expiresDate = new Timestamp(expiresDate);
			return this;
		}
		

		public Builder expiresDate(Timestamp expiresDate) {
			this.expiresDate = expiresDate;
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
		
		public Builder subscriptionId(String subscriptionId) {
			this.subscriptionId = subscriptionId;
			return this;
		}

		public IOSSubscription build() {
			return new IOSSubscription(status, expiresDate, latestReceiptData, originalTransactionId, subscriptionId);
		}
	}

}
