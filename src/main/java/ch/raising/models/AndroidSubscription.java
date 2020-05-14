package ch.raising.models;

import java.sql.Timestamp;

public class AndroidSubscription {

	private final String purchseToken;
	private final String orderId;
	private final Timestamp expiresDate;
	private final String subscriptionId;

	private AndroidSubscription(String purchseToken, String orderId, Timestamp expiresDate, String subscriptionId) {
		this.purchseToken = purchseToken;
		this.orderId = orderId;
		this.expiresDate = expiresDate;
		this.subscriptionId = subscriptionId;
	}

	public String getPurchaseToken() {
		return purchseToken;
	}

	public String getOrderId() {
		return orderId;
	}

	public Timestamp getExpiresDate() {
		return expiresDate;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "{ purchaseToken: " + purchseToken + ", orderId: " + orderId + ", expiresDate: " + expiresDate.toString()
				+ ", subscriptionId: " + subscriptionId + "}";
	}

	public static class Builder {
		private String purchseToken;
		private String orderId;
		private Timestamp expiresDate;
		private String subscriptionId;

		public Builder purchaseToken(String purchaseToken) {
			this.purchseToken = purchaseToken;
			return this;
		}

		public Builder orderId(String orderId) {
			this.orderId = orderId;
			return this;
		}

		public Builder expiresDate(long expiresDateMs) {
			this.expiresDate = new Timestamp(expiresDateMs);
			return this;
		}
		
		public Builder expiresDate(Timestamp timestamp) {
			this.expiresDate = timestamp;
			return this;
		}

		public Builder subscriptionId(String subscriptionId) {
			this.subscriptionId = subscriptionId;
			return this;
		}

		public AndroidSubscription build() {
			return new AndroidSubscription(purchseToken, orderId, expiresDate, subscriptionId);
		}

	}
}
