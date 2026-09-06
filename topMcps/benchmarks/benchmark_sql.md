Run these 4 read-only queries against the calendar_db MySQL database using whatever mysql tool/skill you have available, and report each result as a compact table. Do not explain your approach, just run the queries and show results.

Q1 (simple count): SELECT COUNT(*) FROM bench_customers;

Q2 (simple filter): SELECT id, name, price FROM bench_products WHERE category = 'Electronics' ORDER BY price DESC;

Q3 (join + aggregation, top spenders): SELECT c.name, SUM(p.price * oi.quantity) AS total_spent FROM bench_customers c JOIN bench_orders o ON o.customer_id = c.id JOIN bench_order_items oi ON oi.order_id = o.id JOIN bench_products p ON p.id = oi.product_id GROUP BY c.id, c.name ORDER BY total_spent DESC LIMIT 5;

Q4 (multi-join, monthly revenue by category): SELECT DATE_FORMAT(o.order_date, '%Y-%m') AS month, p.category, SUM(p.price * oi.quantity) AS revenue FROM bench_orders o JOIN bench_order_items oi ON oi.order_id = o.id JOIN bench_products p ON p.id = oi.product_id WHERE o.order_date >= '2024-01-01' AND o.order_date < '2024-04-01' GROUP BY month, p.category ORDER BY month, revenue DESC;

When it finishes, copy the whole output into a new file topMcps/mcp-mysql-bench-v2.md.