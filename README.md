![The Well App](image/bookShop-pet-logo.png)

<br />

---

# About the project

- This project is the first practical experience in writing RESTful applications. 
- It is written entirely in java language, using N-Tier architecture and using MySQL DB.
This practice allowed me to get experience with the Spring Boot framework, as well as learn about the concepts of DTO and mapper. 
- It introduced me to the basic concepts of web application security and the possibility of using JWT token, 
taught me how to use liquibase and Docker and how to write tests for different application layers. 
---
>You can see an example of the project structure, using tech stack and project functionality on slides below ⬇️

# ⬇ N-Tier project structure ⬇
![The Architectural Structure](image/architecturalStructure.png)

---

# ⬇ Tech stack used to create the application ⬇
![Using Technologies](image/usingTechnologies.png)

> Examples of endpoint queries are shown below.
> Swagger was used for the examples.

## 🔹 User management
<details>
<summary></summary>
<details>
<summary>
🔹 User registration endpoint
</summary>
  <img src='image/user/registerRequest.png'/>
  <img src='image/user/registerResponse.png'/>
</details>
<details>
<summary>
🔹 User login endpoint
</summary>
  <img src='image/user/loginUser.png'/>
</details>
</details>

## 🔹 Categories management
<details>
<summary></summary>
<details>
<summary>
🔹 Add new category
</summary>
  <img src='image/category/addCategory.png'/>
</details>
<details>
<summary>
🔹 Get all categories
</summary>
  <img src='image/category/getAllCategories.png'/>
</details>
<details>
<summary>
🔹 Get category by its id
</summary>
  <img src='image/category/getCategoryById.png'/>
</details>
<details>
<summary>
🔹 Get books by category id
</summary>
  <img src='image/category/getBooksByCategoryId.png'/>
</details>
<details>
<summary>
🔹 Update category
</summary>
  <img src='image/category/updateCategoryById.png'/>
</details>
<details>
<summary>
🔹 Delete category
</summary>
  <img src='image/category/deleteCategory.png'/>
</details>
</details>

## 🔹 Books management
<details>
<summary></summary>
<details>
<summary>
🔹 Add new book
</summary>
  <img src='image/book/addBookRequest.png'/>
  <img src='image/book/addBookResponse.png'/>
</details>
<details>
<summary>
🔹 Get all books
</summary>
  <img src='image/book/getBooks.png'/>
</details>
<details>
<summary>
🔹 Get book by its id
</summary>
  <img src='image/book/getBookById.png'/>
</details>
<details>
<summary>
🔹 Update book
</summary>
  <img src='image/book/updateBookRequest.png'/>
  <img src='image/book/updateBookResponse.png'/>
</details>
<details>
<summary>
🔹 Delete book
</summary>
  <img src='image/book/deleteBookBuId.png'/>
</details>
</details>

## 🔹 Shopping cart management
<details>
<summary></summary>
<details>
<summary>
🔹 Add book to cart
</summary>
  <img src='image/cart/addBookToShoppingCart.png'/>
</details>
<details>
<summary>
🔹 Get all cart items
</summary>
  <img src='image/cart/getCartItems.png'/>
</details>
<details>
<summary>
🔹 Update number of books in shopping cart
</summary>
  <img src='image/cart/updateNumberOfBooks.png'/>
</details>
<details>
<summary>
🔹 Delete book from cart
</summary>
  <img src='image/cart/deleteBookFromCart.png'/>
</details>
</details>

## 🔹 Order management
<details>
<summary></summary>
<details>
<summary>
🔹 Place order
</summary>
  <img src='image/order/placeOrder.png'/>
</details>
<details>
<summary>
🔹 Get orders history
</summary>
  <img src='image/order/ordersHistory1.png'/>
  <img src='image/order/ordersHistory2.png'/>
</details>
<details>
<summary>
🔹 Get order items
</summary>
  <img src='image/order/getOrderItems.png'/>
</details>
<details>
<summary>
🔹 Get order item by id
</summary>
  <img src='image/order/getOrderItemById.png'/>
</details>
<details>
<summary>
🔹 Update order status
</summary>
  <img src='image/order/updateOrderSatus.png'/>
</details>
</details>

# 💻 Required technologies to get up and running the project
- #### JDK 17(or higher)
- #### IntelliJ IDEA
- #### Maven
- #### MySQL
- #### Docker

# 🛠️ How to clone the project and run
- #### Clone project from gitHub
```
git clone https://github.com/dmarmul/book-shop.git
``` 

- #### Add .env file to root directory 
<details>
<summary>
</summary>
  <img src='image/envSettings.png'/>
</details>

- #### Create a Docker image
```
docker build -t book-shop .
``` 
- #### Builds all images
```
docker-compose build
``` 
- #### Start the docker container
```
docker-compose up
``` 

> 🎯 Then you can use all the endpoints specified in the examples above.

# 👨‍💻 Project outcomes

>It was very interesting to deal with the pet project and technologies for the first time, although it was not easy to understand everything at once.
> 
>This project introduced me to web development and motivated me to write a more complex and interesting project. 
It also gave me more motivation to get a better understanding of development technologies and to get my first job in the future.

### 📲 Quick Contact Links

<a href='https://t.me/BaGer_1'>

<img src='https://img.shields.io/badge/Telegram-blue?style=for-the-badge'>

</a>

<a href='https://github.com/dmarmul'>

<img src='https://img.shields.io/badge/GitHub-black?style=for-the-badge'>

</a>

<a href='https://www.linkedin.com/in/%D0%B4%D0%B8%D0%BC%D0%B0-%D0%BC%D0%B0%D1%80%D0%BC%D1%83%D0%BB%D1%8C-479ab6332'>

<img src='https://img.shields.io/badge/LinkedIn-blue?style=for-the-badge'>

</a>

<br />

---

# 👨‍🎓🌐

Thank you for taking the time to review this project. 
If you are interested in cooperation or want to know more about the project, 
please email me at dmarmul228@gmail.com or in the contacts I have left above. 