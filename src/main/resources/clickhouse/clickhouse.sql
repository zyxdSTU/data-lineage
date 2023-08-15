-- 情况一
INSERT INTO `3_5_290`.dim_gender_df_list
SELECT t0.code AS code,
       t1.value AS value,
    '20221230' AS ds
FROM
    (
    SELECT code
    FROM `2_5_221`.s_gender
    ) AS t0
    GLOBAL INNER JOIN
    (
    SELECT
    value AS value,
    code
    FROM `2_5_221`.s_gender
    ) AS t1
ON t0.code = t1.code
--情况二
insert into course_temp
select student_name,
       school_name,
       course_name,
       course_record.*
from course_record global join course
on course_record.course_id = course.course_id
    global join
    (
    select
    *
    from student
    global join school
    on student.school_id = school.school_id
    ) ss
    on course_record.student_id = ss.student_id;

--情况三
insert into 3_5_290.dim_gender_df_list
select t0.code, concat(t0.code, '-', value), '20230507' AS ds
from (select code
      from 2_5_221.s_gender) AS t0
         left join
         (select code, value from 2_5_221.s_gender) AS t1
         on t0.code = t1.code;

--情况四
INSERT INTO student
(id, name, school_name)
SELECT
    student_test.id,
    student_test.name,
    school.name
from student_test
    global join school
on student_test.school_id = school.id;