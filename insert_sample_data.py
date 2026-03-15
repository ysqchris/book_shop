import pymysql
import sys

# Database connection config (matches application.yml)
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'used_book_db',
    'charset': 'utf8mb4'
}

def insert_sample_data():
    conn = None
    try:
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✅ Connected to MySQL successfully!")

        # ---- Insert categories ----
        categories = [
            (1,  '文学小说', 0, 1, 1,  'icon-literature',  '文学类小说书籍',  0),
            (2,  '科学技术', 0, 1, 2,  'icon-science',     '科学技术类书籍',  0),
            (3,  '教材教辅', 0, 1, 3,  'icon-textbook',    '教材和教辅书籍',  0),
            (4,  '生活艺术', 0, 1, 4,  'icon-art',         '生活艺术类书籍',  0),
            (5,  '儿童读物', 0, 1, 5,  'icon-children',    '儿童读物书籍',    0),
            (6,  '中国文学', 1, 2, 1,  'icon-cn-lit',      '中国文学小说',    0),
            (7,  '外国文学', 1, 2, 2,  'icon-foreign-lit', '外国文学小说',    0),
            (8,  '计算机',   2, 2, 1,  'icon-computer',    '计算机技术书籍',  0),
            (9,  '医学',     2, 2, 2,  'icon-medical',     '医学类书籍',      0),
            (10, '大学教材', 3, 2, 1,  'icon-college',     '大学教材书籍',    0),
            (11, '高中教材', 3, 2, 2,  'icon-highschool',  '高中教材书籍',    0),
        ]
        cat_sql = """
            INSERT IGNORE INTO categories
                (id, name, parent_id, level, sort_order, icon, description, status, create_time, update_time, deleted)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW(), 0)
        """
        cursor.executemany(cat_sql, categories)
        print(f"📂 Categories: {cursor.rowcount} rows inserted")

        # ---- Insert admin user (password: 123456, bcrypt) ----
        user_sql = """
            INSERT IGNORE INTO users
                (id, username, password, email, phone, real_name, role, status, create_time, update_time, deleted)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW(), 0)
        """
        users = [
            (1, 'admin',
             '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
             'admin@bookshop.com', '13800138000', '书店管理员', 1, 0),
        ]
        cursor.executemany(user_sql, users)
        print(f"👤 Users: {cursor.rowcount} rows inserted")

        # ---- Insert books ----
        books = [
            # 余华经典书单
            (1, '活着', '余华', '9787506365437', '作家出版社', '2012-08-01', 6,
             22.00, 35.00, 50,
             'https://img3.doubanio.com/view/subject/s/public/s29053580.jpg',
             '《活着》是余华的代表作之一，讲述了农村人福贵悲惨的人生遭遇。福贵本是个阔少爷，可他嗜赌如命，终于在一夜之间将家产输得精光，父亲因此被气死，母亲则在穷困中患了重病，妻子家珍含辛茹苦带大了一双儿女，但女儿不幸变成了哑巴，儿子机灵活泼……然而，真正的悲剧从此才开始渐次上演。这是一部关于生命与死亡、苦难与坚韧的史诗级作品。',
             0, 1),
            (2, '许三观卖血记', '余华', '9787506366960', '作家出版社', '2013-03-01', 6,
             25.00, 39.00, 40,
             'https://img2.doubanio.com/view/subject/s/public/s1074291.jpg',
             '《许三观卖血记》是余华的长篇小说，以博大的温情描绘了磨难中的人生，以激烈的故事形式表达了人在面对厄运时求生的欲望。许三观是城里丝厂的送茧工，他靠着卖血渡过了人生的一个个难关——娶妻、还债、度荒年、救儿子……这是一部关于父爱、家庭与生命尊严的感人之作。',
             0, 1),
            (3, '兄弟', '余华', '9787506362641', '作家出版社', '2012-03-01', 6,
             39.00, 59.00, 30,
             'https://img1.doubanio.com/view/subject/s/public/s4371408.jpg',
             '《兄弟》是余华的长篇小说，讲述了江南小镇两兄弟李光头和宋钢的人生故事。小说分为上下两册，上册描写了文化大革命时期的苦难，下册则描写了改革开放后的荒诞现实。这是一部关于中国近现代历史变迁、人性善恶与兄弟情义的宏大叙事。',
             0, 1),
            (4, '在细雨中呼喊', '余华', '9787506366977', '作家出版社', '2013-03-01', 6,
             22.00, 32.00, 35,
             'https://img3.doubanio.com/view/subject/s/public/s1074290.jpg',
             '《在细雨中呼喊》是余华的第一部长篇小说，以一个孩子的视角，描述了一个江南小镇上的童年往事。小说充满了对童年的追忆与反思，展现了成长过程中的孤独、恐惧与渴望。这是一部关于记忆、时间与成长的诗意之作，被誉为余华最具文学价值的作品之一。',
             0, 1),
            (5, '第七天', '余华', '9787536075627', '新星出版社', '2013-06-01', 6,
             28.00, 39.80, 45,
             'https://img3.doubanio.com/view/subject/s/public/s26651903.jpg',
             '《第七天》是余华2013年出版的长篇小说，以荒诞的笔触描绘了一个死去的人在七天里的游历。主人公杨飞死后游荡在现实与死亡之间，遇见了各种各样的亡灵，拼凑出一幅当代中国社会的众生相。这是一部关于生死、爱情与社会现实的深刻反思之作。',
             0, 1),
            (6, '文城', '余华', '9787530222690', '北京十月文艺出版社', '2021-03-01', 6,
             49.50, 59.00, 60,
             'https://img2.doubanio.com/view/subject/s/public/s33956867.jpg',
             '《文城》是余华2021年出版的长篇小说，讲述了清末民初年间，一个叫林祥福的男人，带着一块土地契约，千里寻妻的故事。小说以宏大的历史背景为底色，描绘了那个动荡年代里普通人的命运与情感。这是余华时隔多年后的重磅回归之作，延续了他一贯的史诗风格。',
             0, 1),
            # 其他样板书
            (7, '三体', '刘慈欣', '9787536692930', '重庆出版社', '2008-01-01', 6,
             25.00, 45.00, 10,
             'https://img2.doubanio.com/view/subject/s/public/s2768378.jpg',
             '《三体》是刘慈欣创作的系列科幻小说，讲述了地球文明与三体文明的信息交流、生死厮杀及两个文明在宇宙中的兴衰历程。',
             0, 1),
            (8, 'Java编程思想', 'Bruce Eckel', '9787111213826', '机械工业出版社', '2007-06-01', 8,
             45.00, 89.00, 8, None,
             'Java编程经典教材，深入讲解Java语言的核心概念与编程思想。',
             0, 1),
            (9, '高等数学', '同济大学数学系', '9787040396638', '高等教育出版社', '2014-07-01', 10,
             30.00, 65.00, 20, None,
             '大学高等数学教材，全国高校广泛使用的经典教材。',
             0, 1),
        ]
        book_sql = """
            INSERT IGNORE INTO books
                (id, title, author, isbn, publisher, publish_date, category_id,
                 price, original_price, stock, cover_image, description, status, seller_id,
                 create_time, update_time, deleted)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW(), 0)
        """
        cursor.executemany(book_sql, books)
        print(f"📚 Books: {cursor.rowcount} rows inserted")

        conn.commit()
        print("\n✅ Sample data inserted successfully!")
        print("\n📖 Yu Hua's books in database:")
        cursor.execute(
            "SELECT id, title, price, stock FROM books WHERE author = '余华' AND deleted = 0 ORDER BY id"
        )
        for row in cursor.fetchall():
            print(f"   [{row[0]}] {row[1]}  ¥{row[2]}  (stock: {row[3]})")

    except pymysql.err.OperationalError as e:
        print(f"\n❌ Cannot connect to MySQL: {e}")
        print("   Please make sure MySQL is running and the database 'used_book_db' exists.")
        print("   Run docs/database.sql first to create the database and tables.")
        sys.exit(1)
    except pymysql.Error as e:
        print(f"\n❌ MySQL Error: {e}")
        if conn:
            conn.rollback()
        sys.exit(1)
    finally:
        if conn:
            conn.close()

if __name__ == '__main__':
    insert_sample_data()
