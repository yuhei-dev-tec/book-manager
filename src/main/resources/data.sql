insert into book_manager(book_title, author_name, rating)
select 'カッコウの卵は誰のもの？','東野圭吾', 4
WHERE NOT EXISTS(
    select 1 from book_manager where book_title = 'カッコウの卵は誰のもの？'
);
insert into book_manager(book_title, author_name, rating)
select '天空の蜂','東野圭吾', 4
WHERE NOT EXISTS(
    select 1 from book_manager where book_title = '天空の蜂'
);
