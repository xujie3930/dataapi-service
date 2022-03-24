//生成api历史表数据
INSERT INTO `dataapi`.`icredit_api_base_hi` (`id`, `type`, `name`, `path`, `request_type`, `response_type`, `desc`, `api_group_id`, `api_version`, `publish_status`, `publish_user`, `publish_time`, `remark`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`, `api_base_id`)
SELECT api.`id`, api.`type`, api.`name`, api.`path`, api.`request_type`, api.`response_type`, api.`desc`, api.`api_group_id`, api.`api_version`, api.`publish_status`, api.`publish_user`, api.`publish_time`, api.`remark`, api.`create_time`, api.`create_by`, api.`update_time`, api.`update_by`, api.`del_flag`,(SELECT `id` FROM `dataapi`.`icredit_api_base` where `id` = api.`id`) as api_base_id from `dataapi`.`icredit_api_base` api WHERE api.api_version = 1 OR api.api_version IS null;

ALTER TABLE `dataapi`.`icredit_api_base` ADD interface_source tinyint(1) default 0 COMMENT 'API来源：0-内部，1-外部';
UPDATE `dataapi`.`icredit_api_base` SET interface_source = 0 WHERE interface_source IS NULL;

ALTER TABLE `dataapi`.`icredit_api_base_hi` ADD interface_source tinyint(1) default 0 COMMENT 'API来源：0-内部，1-外部';
UPDATE `dataapi`.`icredit_api_base_hi` SET interface_source = 0 WHERE interface_source IS NULL;

ALTER TABLE `dataapi`.`icredit_api_param` MODIFY COLUMN default_value VARCHAR(120);