import random

categories = {
    'CPU': [
        'Intel Core i3-12100F', 'Intel Core i5-12400F', 'Intel Core i5-13600K', 'Intel Core i7-13700K', 'Intel Core i9-13900K',
        'Intel Core i5-14600K', 'Intel Core i7-14700K', 'Intel Core i9-14900K', 'Intel Pentium Gold G7400', 'Intel Core i3-13100',
        'AMD Ryzen 5 5600G', 'AMD Ryzen 5 5600X', 'AMD Ryzen 7 5700X', 'AMD Ryzen 7 5800X3D', 'AMD Ryzen 9 5950X',
        'AMD Ryzen 5 7600', 'AMD Ryzen 7 7700X', 'AMD Ryzen 9 7900X', 'AMD Ryzen 9 7950X3D', 'AMD Threadripper 5995WX'
    ],
    'RAM': [
        'RAM 8GB DDR4 3200MHz Kingston Fury', 'RAM 16GB DDR4 3200MHz Corsair Vengeance', 'RAM 32GB DDR4 3200MHz G.Skill Trident Z',
        'RAM 8GB DDR4 2666MHz Samsung', 'RAM 16GB DDR4 2666MHz Hynix', 'RAM 8GB DDR5 4800MHz Crucial',
        'RAM 16GB DDR5 5200MHz Kingston Fury Beast', 'RAM 32GB DDR5 6000MHz Corsair Dominator Platinum',
        'RAM 16GB DDR5 5600MHz G.Skill Ripjaws S5', 'RAM 64GB DDR5 6400MHz TeamGroup T-Force',
        'RAM Laptop 8GB DDR4 3200MHz Samsung', 'RAM Laptop 16GB DDR4 3200MHz Crucial', 'RAM Laptop 8GB DDR3L 1600MHz Kingston',
        'RAM Laptop 16GB DDR5 4800MHz Hynix', 'RAM Laptop 32GB DDR5 5600MHz Corsair'
    ],
    'Mainboard': [
        'Mainboard Asus Prime B760M-K D4', 'Mainboard Gigabyte B760M DS3H AX', 'Mainboard MSI MAG B760M Mortar WiFi',
        'Mainboard ASRock B760M Pro RS', 'Mainboard Asus ROG Strix B760-G Gaming WiFi',
        'Mainboard Gigabyte Z790 AORUS ELITE AX', 'Mainboard MSI MPG Z790 Edge WiFi', 'Mainboard Asus ROG Maximus Z790 Hero',
        'Mainboard Asus TUF Gaming B550M-Plus', 'Mainboard MSI B550-A Pro', 'Mainboard Gigabyte X570S AORUS Master',
        'Mainboard ASRock X670E Taichi', 'Mainboard MSI MEG X670E ACE', 'Mainboard Asus Prime H610M-E'
    ],
    'Storage': [
        'SSD 250GB Kingston NV2 NVMe', 'SSD 500GB Kingston NV2 NVMe', 'SSD 1TB Kingston NV2 NVMe',
        'SSD 500GB Samsung 970 EVO Plus', 'SSD 1TB Samsung 980 Pro', 'SSD 2TB Samsung 990 Pro',
        'SSD 500GB WD Blue SN580', 'SSD 1TB WD Black SN850X', 'SSD 2TB Crucial P3 Plus',
        'SSD 120GB WD Green SATA', 'SSD 240GB Kingston A400 SATA', 'SSD 480GB SanDisk Ultra 3D',
        'HDD 1TB Seagate Barracuda', 'HDD 2TB Seagate Barracuda', 'HDD 1TB WD Blue', 'HDD 4TB WD Red Plus',
        'HDD 6TB Toshiba Enterprise'
    ],
    'GPU': [
        'VGA Asus Dual GeForce RTX 3050 8GB', 'VGA Gigabyte Eagle GeForce RTX 3060 12GB', 'VGA MSI Ventus 2X RTX 4060 8GB',
        'VGA Asus TUF Gaming RTX 4060 Ti 8GB', 'VGA Zotac Gaming RTX 4070 Twin Edge', 'VGA Gigabyte Gaming OC RTX 4070 Ti 12GB',
        'VGA MSI Suprim X RTX 4080 16GB', 'VGA Asus ROG Strix RTX 4090 24GB',
        'VGA ASRock Challenger Radeon RX 6600 8GB', 'VGA Sapphire Pulse Radeon RX 7600', 'VGA PowerColor Red Devil RX 7800 XT',
        'VGA MSI Gaming X Slim RTX 4070 Super', 'VGA Gigabyte Aero OC RTX 4080 Super'
    ],
    'Screen': [
        'Màn hình 24 inch Dell P2422H IPS', 'Màn hình 24 inch LG 24QP500 2K 75Hz', 'Màn hình 27 inch Asus TUF VG27AQ 165Hz',
        'Màn hình 27 inch Samsung Odyssey G5', 'Màn hình 24 inch ViewSonic VX2428 165Hz',
        'Màn hình 32 inch Gigabyte M32U 4K 144Hz', 'Màn hình Laptop 15.6 inch FHD 30pin 60Hz',
        'Màn hình Laptop 15.6 inch FHD IPS 144Hz 40pin', 'Màn hình Laptop 14.0 inch FHD Slim 30pin',
        'Màn hình Laptop 13.3 inch Retina LCD Assembly', 'Màn hình Laptop 15.6 inch 4K OLED',
        'Màn hình Laptop 17.3 inch FHD 144Hz'
    ],
    'Keyboard': [
        'Bàn phím cơ DareU EK87', 'Bàn phím cơ Logitech G Pro X TKL', 'Bàn phím cơ Corsair K70 RGB Pro',
        'Bàn phím cơ Razer BlackWidow V4', 'Bàn phím cơ Keychron K6 Pro Wireless', 'Bàn phím cơ Akko 3068B Plus',
        'Bàn phím Laptop Dell Inspiron 15 3000 Series', 'Bàn phím Laptop Dell Vostro 5468',
        'Bàn phím Laptop HP Pavilion 15-cs', 'Bàn phím Laptop HP EliteBook 840 G3',
        'Bàn phím Laptop Asus Vivobook X515', 'Bàn phím Laptop Lenovo ThinkPad T480',
        'Bàn phím Laptop Acer Nitro 5 AN515', 'Bàn phím Laptop MacBook Pro A1706'
    ],
    'Touchpad': [
        'Touchpad Dell XPS 13 9360', 'Touchpad Dell Latitude 7480', 'Touchpad HP EliteBook 840 G5',
        'Touchpad HP Spectre x360', 'Touchpad Asus ZenBook 14 UX425', 'Touchpad Lenovo ThinkPad X1 Carbon Gen 6',
        'Touchpad MacBook Air A1466', 'Touchpad MacBook Pro A1708', 'Touchpad Apple Magic Trackpad 2',
        'Touchpad Microsoft Surface Laptop 3'
    ],
    'Battery': [
        'Pin Laptop Dell WDX0R 42Wh', 'Pin Laptop Dell 60Wh 4-cell', 'Pin Laptop HP HT03XL', 'Pin Laptop HP CS03XL',
        'Pin Laptop Asus C31N1816', 'Pin Laptop Lenovo L17M3P52', 'Pin Laptop Acer AC14B8K',
        'Pin MacBook Air A1466 Original', 'Pin MacBook Pro A1713', 'Pin MacBook Pro A2338 M1'
    ],
    'Webcam': [
        'Webcam Logitech C270 HD', 'Webcam Logitech C920e Pro FHD', 'Webcam Logitech Brio 4K',
        'Webcam Razer Kiyo Ring Light', 'Webcam Rapoo C260', 'Webcam HIKVISION DS-U02',
        'Cụm Camera Laptop Dell Inspiron', 'Cụm Camera Laptop HP Pavilion', 'Cụm Camera MacBook Air M1'
    ],
    'Speaker': [
        'Loa Vi Tính Logitech Z120 Stereo', 'Loa Vi Tính Logitech Z213 2.1', 'Loa Bluetooth Sony SRS-XB13',
        'Loa Bluetooth JBL Flip 6', 'Loa Soundbar Samsung T420',
        'Loa Laptop Dell Inspiron 7559', 'Loa Laptop Dell XPS 15 9560', 'Loa Laptop HP Pavilion 14-ce',
        'Loa Laptop MacBook Pro 13 2017', 'Loa Laptop Asus ROG Strix G531'
    ]
}

print('INSERT IGNORE INTO parts (name, description, price, stock) VALUES')
all_values = []
for cat, items in categories.items():
    for item in items:
        price = random.randint(15, 500) * 10000  # 150k - 5tr range base, tweaked logic below
        stock = random.randint(30, 80)
        
        # Specific pricing logic overrides
        if 'Core i9' in item or 'Ryzen 9' in item or 'RTX 4090' in item:
            price = random.randint(1000, 4000) * 10000
        elif 'Core i7' in item or 'Ryzen 7' in item or 'RTX 4070' in item or 'Mainboard Z790' in item:
            price = random.randint(500, 1500) * 10000
        elif 'RAM 32GB' in item or 'SSD 2TB' in item:
             price = random.randint(200, 500) * 10000
        elif 'Bàn phím Laptop' in item or 'Touchpad' in item:
             price = random.randint(20, 100) * 10000

        desc = f'{cat} Lỗi đổi mới, bảo hành 12-36 tháng.'
        all_values.append(f"('{item}', '{desc}', {price}, {stock})")

print(',\n'.join(all_values) + ';')
