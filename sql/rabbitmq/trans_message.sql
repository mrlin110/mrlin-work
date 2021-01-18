DROP TABLE IF EXISTS `trans_message`;
CREATE TABLE `trans_message`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '��Ϣid',
  `service` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '��������',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ϣ����',
  `exchange` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '������',
  `routingKey` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '·�ɼ�',
  `queue` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '���',
  `sequence` int(0) NULL DEFAULT NULL COMMENT '����',
  `payload` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '��Ϣ����',
  `date` datetime(0) NULL DEFAULT NULL COMMENT 'ʱ��',
  PRIMARY KEY (`id`, `service`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT='��Ϣ��';

SET FOREIGN_KEY_CHECKS = 1;