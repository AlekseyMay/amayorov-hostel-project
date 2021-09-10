create table user_role (
                           user_id bigint not null,
                           role_id bigint not null,
                           primary key (role_id, user_id)
)

GO

alter table user_role
    add constraint FKa68196081fvovjhkek5m97n3y
        foreign key (role_id)
            references role

GO

alter table user_role
    add constraint FK859n2jvi8ivhui0rl0esws6o
        foreign key (user_id)
            references user

GO