 CREATE TABLE "authorities" (
  "username" varchar(50) NOT NULL,
  "authority" varchar(50) NOT NULL,
  UNIQUE KEY "authorities_idx_1" ("username","authority"),
  CONSTRAINT "authorities_ibfk_1"
  FOREIGN KEY ("username")
  REFERENCES "user" ("username")
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

 CREATE TABLE "authorities" (
  "username" varchar(50) NOT NULL,
  "authority" varchar(50) NOT NULL,
  UNIQUE KEY "authorities_idx_1" ("username","authority"),
  CONSTRAINT "authorities_ibfk_1"
  FOREIGN KEY ("username")
  REFERENCES "user" ("username")
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;