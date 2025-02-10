package com.payment.model;

import java.io.Serializable;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@SelectBeforeUpdate(value=true)
@DynamicUpdate(value=true)
@DynamicInsert(value=true)
@Data
@AllArgsConstructor
@Table(name = "PAYMENT_TBL")
public class Payment implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "player_seq")
	@SequenceGenerator(name="player_seq",sequenceName = "player_sequence")
	@Column(name = "id")
	private int id;
	@Column(name = "paymentserviceprovider")
	private String paymentserviceprovider;
	@Column(name = "totalpayout")
	private int totalpayout;
	@Column(name = "quantity")
	private int quantity;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPaymentserviceprovider() {
		return paymentserviceprovider;
	}

	public void setPaymentserviceprovider(String paymentserviceprovider) {
		this.paymentserviceprovider = paymentserviceprovider;
	}

	public int getTotalpayout() {
		return totalpayout;
	}

	public void setTotalpayout(int totalpayout) {
		this.totalpayout = totalpayout;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", paymentserviceprovider=" + paymentserviceprovider + ", totalpayout="
				+ totalpayout + ", quantity=" + quantity + "]";
	}

	public Payment() {
		super();
		// TODO Auto-generated constructor stub
	}

}
