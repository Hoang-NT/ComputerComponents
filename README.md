# PC components Restful API

- /api/signup
- /api/login
- /api/customer
- /api/order
- /api/order-details
- /api/product
- /api/specification
- /api/promotion
- /api/review
- /api/question
- /api/answer

## Anonymous user can:

- Access: "/api/signup"
- Access: "/api/login"
- Access: "/api/product" with GET method.
- Access: "/api/promotion" with GET method.
- Access: "/api/specification" with GET method.
- Access: "/api/review" with GET method.
- Access: "/api/question" with GET method.
- Access: "/api/answer" with GET method.

## User with ROLE_USER can:

- All the rights as anonymous user.
- Access: "/api/customer" but just interact with his own account.
- Access: "/api/order" with methods:
    - GET: get his orders.
    - POST: create an order.
    - PUT: update his order (cannot update after status is delivering).
    - DELETE: cancel (delete) his order (cannot update after status is delivering).
- Access: "/api/order-details" just like api order, and depend on his order ID.
- Access: "/api/review" with other methods but just interact with his own review:
    - POST: create a review.
    - PUT: update his own review only.
    - DELETE: delete his review.
- Access: "/api/question" with other methods but just interact with his own question.
- Access: "/api/answer" with other methods but just interact with his own answer.

## User with ROLE_STAFF can:
- All the rights as ROLE_USER but cannot create review.
- Can control all order and order details.
- Access: "/api/production" with other methods.
- Access: "/api/specification" with other methods.
- Access: "/api/promotion" with other methods.

## User with ROLE_ADMIN can:

- All the rights as ROLE_STAFF and cannot create review.
- Can control other customer account.
- Can control all review, question and answer.
- Can GET deleted data.

## Code:

- "200": success
- "400": data not found (customer, product, etc.)
- "401": authentication error
- "402": date in wrong format
- "403": authorization error
- "404": token error
- "405": duplicate in unique field (username, email, etc.)
- "406": invalid entity - field error
- "407": JSON read/write error
- "408": other error

## Use of API:

- **"/api/signup"** with method **POST**: request with customer information to add a customer. Customer info in request body as follows:

        {
            "username": username_to_create (length min 3 character, max 50),
            "password": password_of_customer (length min 6 character, max 60),
            "fullName": full_name_of_customer,
            "phone": phone_number_of_customer,
            "email": customer_email,
            "address": address_of_customer
        }
    Default role is "ROLE_USER", use root admin account to assign an account to "ROLE_STAFF" or "ROLE_ADMIN". Response data is created customer or null if any error (check code and message).

        {
            "code": "200",
            "message": "Success",
            "data": {
                "username": username_created,
                "fullName": full_name_of_customer,
                "phone": phone_number_of_customer,
                "email": customer_email,
                "address": address_of_customer,
                "role": role_of_customer,
                "createDate": create_date,
                "modifyDate": last_modify_date,
                "lastModifiedBy": last_modify_by,
                "enable": status_of_customer
            }
        }


- **"/api/login"** with method **POST**: request authentication token for customer. Request body just need username and password:

        {
            "username": username_to_authen,
            "password": password_of_customer
        }

    Response data is token or null if any error (check code and message).

        {
            "code": "200",
            "message": "Success",
            "data": created_token
        }


- **"/api/customer":**
    - **GET:** can send with request parameters. Request param available:
        - username: provide username (to find by)
        - email: email of customer (to find by)
        - phone: phone number of customer (to find by)
        - fullName: full name of customer (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of customers in a page, default 10
        - *If no param provided, will find all customers and return first page (page 0) with size 10.*

        Response data can be customer found or null, list of customers or empty list. Notice that list of customers return is paged and sorted (by role and by username ascending).
      
    - **PUT:** request edit a customer. Response data is edited customer or null if any error (check code and message). Customer in request body contain field "username" and any field with new update. User with "ROLE_ADMIN" can assign a user with "ROLE_USER" to "ROLE_STAFF" or "ROLE_ADMIN" by using "role" field.
    - **DELETE:** request delete a customer by giving username in request parameter. Response data is deleted customer or null if not found.


- **"/api/order":**
    - **GET:** can send with request parameters. Request param available:
        - id: ID of order (to find by)
        - createDate: created date of order format YYYY-MM-DD (to find by)
        - owner: id of customer (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of orders in a page, default 10
        - *If no param provided, will find all orders and return first page (page 0) with size 10.*

        Response data can be order found or null, list of orders or empty list. Notice that list of orders return is paged and sorted (by create date descending).

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "id": id_of_order,
                    "createDate": order_created_date,
                    "modifyDate": order_last_modified_date,
                    "status": status_of_order,
                    "createBy": create_by,
                    "lastModifyBy": last_modify_by,
                    "isDeleted": is_order_deleted
                }
            }
          
    - **POST:** request to create an order. Order owner is current logged in customer. Response data is created order or null if any error (check code and message). Order info in request body:

            {
                "status": status_of_order
            }
          
    - **PUT:** request to edit order. Response data is edited order or null if any error (check code and message). Order update in request body:

            {
                "id": id_of_order,
                "status": status_of_order
            }
          
    - **DELETE:** request delete a comment by giving id in request parameter. Response data is deleted order or null if not found.


- **"/api/order-details":**
    - **GET:** can send with request parameters. Request param available:
        - id: ID of order-details (to find by)
        - order: id of order that order-details belongs (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of order-details in a page, default 10
        - *If no param provided, will find all order-details and return first page (page 0) with size 10.*

        Response data can be order-details found or null, list of order-details or empty list. Notice that list of order-details return is paged and sorted (by create date and last modify date descending).

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "id": id_of_order_details,
                    "order": id_of_order,
                    "product": id_of_product,
                    "amount": number_of_product,
                    "total": total_money_of_order_details,
                    "createDate": order_details_created_date,
                    "modifyDate": order_details_last_modified_date,
                    "lastModifyBy": last_modify_by,
                    "isDeleted": is_this_order_details_deleted
                }
            }
          
    - **POST:** request with order-details information to create an order-details. Field total will be computed automatically by: *(price - price\*discount) * amount*. Response data is created order-details or null if any error (check code and message). Order-details info in request body as follows:

            {
                "order": id_of_order,
                "product": id_of_product,
                "amount": number_of_product
            }
          
    - **PUT:** request edit an order-details. Again, field total will be computed automatically. Response data is edited order-details or null if any error (check code and message). Order-details update in request body as follows:

            {
                "id": id_of_order_details,
                "amount": number_of_product
            }
          
    - **DELETE:** request delete an order-details by giving id in request parameter. Response data is deleted order-details or null if not found.


- **"/api/product":**
    - **GET:** can send with request parameters. Request param available:
        - id: ID of product (to find by)
        - name: name of product (to find by)
        - category: category of product (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of products in a page, default 10
        - *If no param provided, will find all products and return first page (page 0) with size 10.*

        Response data can be product found or null, list of products or empty list. Notice that list of products return is paged and sorted (by product name ascending).

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "id": id_of_product,
                    "name": name_of_product,
                    "category": category_of_product,
                    "price": price_of_product,
                    "discount": discount_of_product,
                    "stock": number_of_product_on_stock,
                    "createDate": product_created_date,
                    "modifyDate": product_last_modified_date,
                    "createBy": create_by,
                    "lastModifyBy": last_modify_by,
                    "isDeleted": is_this_product_deleted
                }
            }
          
    - **POST:** request with product information to create a product. Response data is created product or null if any error (check code and message). Product info in request body as follows:

            {
                "name": name_of_product,
                "category": category_of_product,
                "price": price_of_product,
                "discount": discount_of_product,
                "stock": number_of_product_on_stock
            }
          
    - **PUT:** request edit a product. Response data is edited product or null if any error (check code and message). Product update in request body contain "id" and field with new update.
          
    - **DELETE:** request delete a product by giving id in request parameter. This will also delete the specification of product. Response data is deleted product or null if not found.


- **"/api/specification":**
    - **GET:** can send with request parameters. Request param available:
        - productID: ID of product (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of specifications in a page, default 10
        - *If no param provided, will find all specifications and return first page (page 0) with size 10.*

      Response data can be specification found or null, list of specifications or empty list. Notice that list of specifications return is paged and sorted (by created date descending).

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "productID": id_of_product,
                    "productInfo": information_about_product,
                    "designAndWeight": design_and_weight_of_product,
                    "details": details_of_product,
                    "accessoryInBox": accessory_in_the_box,
                    "createdDate": specification_created_date,
                    "lastModifiedDate": specification_last_modified_date,
                    "createdBy": created_by,
                    "lastModifiedBy": last_modified_by,
                    "isDeleted": is_this_specification_deleted
                }
            }

    - **POST:** request with specification information to create. Response data is created spec or null if any error (check code and message). Specification info in request body as follows:

            {
                "productID": id_of_product,
                "productInfo": information_about_product (not empty),
                "designAndWeight": design_and_weight_of_product,
                "details": details_of_product (not empty),
                "accessoryInBox": accessory_in_the_box
            }

    - **PUT:** request edit a specification. Response data is edited spec or null if any error (check code and message). Spec update in request body contain "productID" and field with new update.


- **"/api/promotion":**
    - **GET:** can send with request parameters. Request param available:
        - id: ID of promotion (to find by)
        - product: id of product (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of promotions in a page, default 10
        - *If no param provided, will find all promotions and return first page (page 0) with size 10.*

        Response data can be promotion found or null, list of promotions or empty list. Notice that list of promotions return is paged and sorted by last modified date descending.

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "id": id_of_promotion,
                    "content": content_of_promotion,
                    "active": status_of_promotion,
                    "createDate": promotion_created_date,
                    "modifyDate": promotion_last_modified_date,
                    "product": id_of_product,
                    "createBy": create_by,
                    "lastModifyBy": last_modify_by
                }
            }
          
    - **POST:** request with promotion information to create a promotion. Response data is created promotion or null if any error (check code and message). Promotion info in request body as follows:

            {
                "content": content_of_promotion,
                "active": status_of_promotion(optional - default true),
                "product": id_of_product
            }
          
    - **PUT:** request edit a promotion. Response data is edited promotion or null if any error (check code and message). Promotion update in request body contain "id" and field with new update.
          
    - **DELETE:** request delete a promotion by giving id in request parameter. Response data is deleted promotion or null if not found.


- **"/api/review":**
    - **GET:** can send with request parameters. Request param available:
        - id: ID of review (to find by)
        - product: id of product (to find by)
        - owner: id of customer (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of reviews in a page, default 10
        - *If no param provided, will find all reviews and return first page (page 0) with size 10.*

        Response data can be review found or null, list of reviews or empty list. Notice that list of reviews return is paged and sorted (by create date descending).

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "id": id_of_review,
                    "content": content_of_review,
                    "rating": rating_product,
                    "createDate": review_created_date,
                    "modifyDate": review_last_modified_date,
                    "product": id_of_product,
                    "createBy": create_by,
                    "lastModifyBy": last_modify_by,
                    "isDeleted": is_this_review_deleted
                }
            }
          
    - **POST:** request with review information to create a review. Owner is current logged in customer. Response data is created review or null if any error (check code and message). Review info in request body as follows:

            {
                "content": content_of_review,
                "rating": rating_product(1-5),
                "product": id_of_product
            }
          
    - **PUT:** request edit a review. Response data is edited review or null if any error (check code and message). Review update in request body contain "id" and field with new update.
          
    - **DELETE:** request delete a review by giving id in request parameter. Response data is deleted review or null if not found.


- **"/api/question":**
    - **GET:** can send with request parameters. Request param available:
        - id: ID of question (to find by)
        - product: id of product (to find by)
        - owner: id of customer (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of questions in a page, default 10
        - *If no param provided, will find all questions and return first page (page 0) with size 10.*

        Response data can be question found or null, list of questions or empty list. Notice that list of questions return is paged and sorted (by create date descending).

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "id": id_of_question,
                    "content": content_of_question,
                    "createDate": question_created_date,
                    "modifyDate": question_last_modified_date,
                    "product": id_of_product,
                    "createdBy": username_of_creator,
                    "lastModifiedBy": username_of_last_modified_customer,
                    "isDeleted": is_this_question_deleted
                }
            }
          
    - **POST:** request with question information to create a question. Owner is current logged in customer. Response data is created question or null if any error (check code and message). Question info in request body as follows:

            {
                "content": content_of_question,
                "product": id_of_product
            }
          
    - **PUT:** request edit a review. Response data is edited review or null if any error (check code and message). Review update in request body as follows:

            {
                "id": id_of_question,
                "content": content_of_question
            }
          
    - **DELETE:** request delete a question by giving id in request parameter. Response data is deleted question or null if not found.


- **"/api/answer":**
    - **GET:** can send with request parameters. Request param available:
        - id: ID of answer (to find by)
        - question: id of question (to find by)
        - owner: id of customer (to find by)
        - page: page want to get, default 0 (ex: 1 to return page 1, 5 to return page 5)
        - size: number of answers in a page, default 10
        - *If no param provided, will find all answers and return first page (page 0) with size 10.*

        Response data can be an answer found or null, list of answers or empty list. Notice that list of answers return is paged and sorted (by create date descending).

            {
                "code": "200",
                "message": "Success",
                "data": {
                    "id": id_of_answer,
                    "content": content_of_answer,
                    "createDate": answer_created_date,
                    "modifyDate": answer_last_modified_date,
                    "question": id_of_question,
                    "createdBy": username_of_creator,
                    "lastModifiedBy": username_of_last_modified_customer,
                    "isDeleted": is_this_answer_deleted
                }
            }
          
    - **POST:** request with answer information to create an answer. Owner is current logged in customer. Response data is created answer or null if any error (check code and message). Answer info in request body as follows:

            {
                "content": content_of_answer,
                "question": id_of_question
            }
          
    - **PUT:** request edit an answer. Response data is edited answer or null if any error (check code and message). Answer update in request body as follows:

            {
                "id": id_of_answer,
                "content": content_of_answer
            }
          
    - **DELETE:** request delete a answer by giving id in request parameter. Response data is deleted answer or null if not found.

