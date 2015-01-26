package com.pinthecloud.item.model;

public class ProductTag extends AbstractItemModel<ProductTag> {
	
	public static enum Category {
		Outer("Outer"),
		Shirts("Shirts"),
		Neet("Neet"),
		ManToMan("ManToMan"),
		Hood("Hood"),
		T_Shirts("T_Shirts"),
		Pants("Pants"),
		OnePiece("OnePiece"),
		Skirt("Skirt"),
		Shoes("Shoes"),
		Socks("Socks"),
		Bag("Bag"),
		Accessory("Accessory"),
		Cap("Cap"),
		items("items");
		
		private String value;
		private Category(String value) {
			this.value = value;
		}
		public String toString() {
			return this.value;
		}
	};
	
	private String category;
	private String shopName;
	private String webPage;
	private double price;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category.toString();
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getWebPage() {
		return webPage;
	}
	public void setWebPage(String webPage) {
		this.webPage = webPage;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
}
