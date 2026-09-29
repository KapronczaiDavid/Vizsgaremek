Table users {
  id int [pk, increment]
  loyalty_level_id int [ref: > loyalty_levels.id]
  name varchar(50) [not null]
  email varchar(100) [not null, unique]
  email_verified tinyint [default: 0]
  phone varchar(20) [not null, unique]
  password varchar(255) [not null]
  xp_points int [not null, default: 0]
  created_at datetime [not null, default: `now()`]
  updated_at datetime
  deleted_at datetime
  is_deleted tinyint(1) [not null, default: 0]
}

Table admins {
  id int [pk, increment]
  name varchar(50) [not null]
  email varchar(100) [not null, unique]
  password varchar(255) [not null]
  created_at datetime [not null, default: `now()`]
  updated_at datetime
  deleted_at datetime
  is_deleted tinyint(1) [not null, default: 0]
}

Table user_tokens {
  id int [pk, increment]
  user_id int [not null, ref: > users.id]
  type "enum('password_reset','email_verification')" [not null]
  token_hash varchar(255) [not null, unique]
  expires_at datetime [not null]
  used_at datetime
  created_at datetime [not null, default: `now()`]
}

Table workers {
  id int [pk, increment]
  first_name varchar(50) [not null]
  last_name varchar(50) [not null]
  phone varchar(20) [not null, unique]
  email varchar(100) [unique]
  password varchar(255) [not null]
  position varchar(50)
  active boolean [not null, default: true]
  created_at datetime [not null, default: `now()`]
  updated_at datetime
  deleted_at datetime
  is_deleted tinyint(1) [not null, default: 0]
}

Table vehicles {
  id int [pk, increment]
  user_id int [not null, ref: > users.id]
  brand "enum('alfa_romeo','audi','bmw','chevrolet','citroen','cupra','dacia','ducati','fiat','ford','harley_davidson','honda','hyundai','iveco','jaguar','jeep','kawasaki','kia','land_rover','lexus','man','mazda','mercedes_benz','mini','mitsubishi','nissan','opel','peugeot','porsche','renault','scania','seat','skoda','smart','subaru','suzuki','tesla','toyota','volkswagen','volvo','yamaha','other')"
  license_plate varchar(20)
  vehicle_type "enum('sedan','suv','truck','van','motorcycle','other')"
  created_at datetime [not null, default: `now()`]
  updated_at datetime
  deleted_at datetime
  is_deleted tinyint(1) [not null, default: 0]
}

Table appointments {
  id int [pk, increment]
  user_id int [not null, ref: > users.id]
  vehicle_id int [not null, ref: > vehicles.id]
  worker_id int [ref: > workers.id]
  discount_category "enum('exterior_wash','interior_detailing','polishing','ceramic_coating','full_detailing')"
  appointment_date date [not null]
  appointment_time time [not null]
  status "enum('pending','confirmed','in_progress','completed','cancelled','no_show')" [not null, default: 'pending']
  notes text
  total_price decimal(10,0)
  created_at datetime [not null, default: `now()`]
  updated_at datetime
}

Table appointment_services {
  id int [pk, increment]
  appointment_id int [not null, ref: > appointments.id]
  service_id int [not null, ref: > services.id]
  quantity int [not null, default: 1]
  price decimal(10,0) [not null]
}

Table services {
  id int [pk, increment]
  category "enum('exterior_wash','interior_detailing','polishing','ceramic_coating','full_detailing')" [not null]
  name varchar(100) [not null]
  description text
  price decimal(10,0)
  duration_minutes int
  active boolean [not null, default: true]
  created_at datetime [not null, default: `now()`]
  updated_at datetime
}

Table loyalty_levels {
  id int [pk, increment]
  name varchar(20) [not null, unique]
  min_xp int [not null, unique]
  exterior_wash_flat decimal(10,0) [not null, default: 0]
  interior_detailing_percent decimal(5,2) [not null, default: 0.00]
  polishing_flat decimal(10,0) [not null, default: 0]
  ceramic_coating_percent decimal(5,2) [not null, default: 0.00]
  full_detailing_percent decimal(5,2) [not null, default: 0.00]
}

Table payments {
  id int [pk, increment]
  appointment_id int [not null, unique, ref: > appointments.id]
  amount decimal(10,0)
  discount_amount decimal(10,0) [not null, default: 0]
  payment_status "enum('unpaid','paid','refunded')" [not null, default: 'unpaid']
  payment_date datetime
  collected_by_worker_id int [ref: > workers.id]
  refund_amount decimal(10,0)
  refund_reason text
  created_at datetime [not null, default: `now()`]
  updated_at datetime
}

Table reviews {
  id int [pk, increment]
  user_id int [not null, ref: > users.id]
  appointment_id int [not null, unique, ref: > appointments.id]
  rating int [not null]
  comment text
  created_at datetime [not null, default: `now()`]
}

Table xp_transactions {
  id int [pk, increment]
  user_id int [not null, ref: > users.id]
  appointment_id int [unique, ref: > appointments.id]
  xp_earned int [not null]
  description text
  created_at datetime [not null, default: `now()`]
}
