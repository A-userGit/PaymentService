db.auth('root', 'root');
db.createUser({
    user: 'admin',
    pwd: 'admin',
    roles: [
        {
            role: 'dbOwner',
            db: 'payment_service',
        },
    ],
});
db = new Mongo().getDB("payment_service");
db.createCollection("payments");
db.payments.insertMany([
  { _id: "691485ab7872ebec3bc67fd1", user_id: 1, order_id: 1, status: "APPROVED", timestamp: new Date("2025-11-10"), payment_amount: 200, _class: "com.innowise.paymentservice.entity.Payment"},
  { _id: "691485ab7872ebec3bc67fd2", user_id: 1, order_id: 2, status: "PENDING", timestamp: new Date("2025-11-09"), payment_amount: 100, _class: "com.innowise.paymentservice.entity.Payment"},
  { _id: "691485ab7872ebec3bc67fd3", user_id: 1, order_id: 3, status: "PENDING", timestamp: new Date("2025-11-12"), payment_amount: 300, _class: "com.innowise.paymentservice.entity.Payment"},
  { _id: "691485ab7872ebec3bc67fd4", user_id: 2, order_id: 4, status: "REJECTED", timestamp: new Date("2025-11-12"), payment_amount: 400, _class: "com.innowise.paymentservice.entity.Payment"},
  { _id: "691485ab7872ebec3bc67fd5", user_id: 2, order_id: 5, status: "APPROVED", timestamp: new Date("2025-11-13"), payment_amount: 500, _class: "com.innowise.paymentservice.entity.Payment"}
]);