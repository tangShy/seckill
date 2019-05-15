-- ��ɱִ�д������
DELIMITER $$ -- ���������;ת��Ϊ$$
-- ���崢�����
-- ������ in�������   out�������
-- row_count() ������һ���޸�����sql(delete,insert,update)��Ӱ������
-- row_count:0:δ�޸����� ; >0:��ʾ�޸ĵ������� <0:sql����
CREATE PROCEDURE `seckill`.`execute_seckill`
  (IN v_seckill_id BIGINT, IN v_phone BIGINT,
   IN v_kill_time  TIMESTAMP, OUT r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO success_killed
    (seckill_id, user_phone, state)
    VALUES (v_seckill_id, v_phone, 0);
    SELECT row_count() INTO insert_count;
    IF (insert_count = 0) THEN
      ROLLBACK;
      SET r_result = -1;
    ELSEIF (insert_count < 0) THEN
        ROLLBACK;
        SET r_result = -2;
    ELSE
      UPDATE seckill
      SET number = number - 1
      WHERE seckill_id = v_seckill_id
            AND end_time > v_kill_time
            AND start_time < v_kill_time
            AND number > 0;
      SELECT row_count() INTO insert_count;
      IF (insert_count = 0) THEN
        ROLLBACK;
        SET r_result = 0;
      ELSEIF (insert_count < 0) THEN
          ROLLBACK;
          SET r_result = -2;
      ELSE
        COMMIT;
        SET r_result = 1;
      END IF;
    END IF;
  END;
$$
-- ������̶������
-- ����������¸�Ϊ;
DELIMITER ;

-- ����һ���û�����r_result
SET @r_result = -3;
-- ִ�д������
CALL execute_seckill(1003, 13502178891, now(), @r_result);
-- ��ȡ���
SELECT @r_result;
--------------------- 
���ߣ�����Lewis 
��Դ��CSDN 
ԭ�ģ�https://blog.csdn.net/lewky_liu/article/details/78166080 
��Ȩ����������Ϊ����ԭ�����£�ת���븽�ϲ������ӣ�