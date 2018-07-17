# Books schema

# --- !Ups


CREATE TABLE books (
    book_id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(2000) NOT NULL,
    year varchar(40) NOT NULL,
    PRIMARY KEY (book_id)
);

CREATE TABLE authors (
    author_id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(2000) NOT NULL,
    PRIMARY KEY (author_id)
);

CREATE TABLE booksAndAuthors (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    book_id integer,
    author_id integer,
    PRIMARY KEY (id)
);

INSERT INTO books SET title='TEST', year=1990;
INSERT INTO authors SET name='TESTOVICH';
INSERT INTO booksAndAuthors SET book_id=1, author_id=1;



ALTER TABLE public.books ADD CONSTRAINT un_book_id_constraint UNIQUE (book_id);
ALTER TABLE public.authors ADD CONSTRAINT un_author_id_constraint UNIQUE (author_id);
ALTER TABLE public.booksAndAuthors ADD CONSTRAINT un_id_constraint UNIQUE (id);


ALTER TABLE booksAndAuthors
ADD CONSTRAINT book_id_fk_constraint
FOREIGN KEY (book_id) REFERENCES books (book_id)
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE booksAndAuthors
ADD CONSTRAINT author_id_fk_constraint
FOREIGN KEY (author_id) REFERENCES authors (author_id)
ON UPDATE CASCADE ON DELETE CASCADE;

# --- !Downs

DROP TABLE books;
DROP TABLE booksAndAuthors;
DROP TABLE authors;



