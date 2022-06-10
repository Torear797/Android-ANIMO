<?php


class MobileapiController extends ControllerBase
{
    private $token = "";
    private $userToken = "";
    private $isAuth = false;

    public function initialize()
    {
        $this->view->disable();

        $token = $this->request->getHeader("Authorization");

        if ($token) {
            $this->token = $token;
            $this->userToken = UserTokens::findFirstByToken($this->token);
            $this->isAuth = $this->userToken->isAuth();
        }

    }

    public function reLoginAction()
    {
        $refreshToken = $this->request->getPost("refreshToken");

        $userToken = UserTokens::findFirst('token = "' . $this->token . '" AND refreshToken = "' . $refreshToken . '"');

        if (!$userToken) {
            return json_encode(array('status' => 500, 'text' => 'Database error'), JSON_UNESCAPED_UNICODE);
        } else {
            if ($userToken->isRefreshTokenActive()) {
                return $this->getToken($userToken->getUserId(), $userToken, $this->request->getPost("device_info"), $this->request->getPost("type_device"));
            } else {
                return json_encode(array('status' => 500, 'text' => 'Database error'), JSON_UNESCAPED_UNICODE);
            }
        }
    }

    /**Получение списка препаратов*/
    public function getMedicationsListAction()
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $arPreparations = [];

        $user = $this->userToken->Users;

        $roles = $user->UsersRole;

        $arRoles = [];
        foreach ($roles as $role) {
            $arRoles[] = $role->Roles->name;
        }//foreach

        if (array_intersect(["brand_manager"], $arRoles)) {

            $bmPreparats = BrandManagerPreparats::findByIdUser($user->id);

            if (!count($bmPreparats)) {
                return json_encode(array('status' => 405, 'text' => 'За вами не закреплены препараты, пожалуйста обратитесь к администратору сайта!'), JSON_UNESCAPED_UNICODE);
            }//if

            foreach ($bmPreparats as $bmpRow) {
                $preparat = Preparation::findFirstById($bmpRow->getIdPreparat());
                $arPreparations[$preparat->getId()]['id'] = $preparat->getId();
                $arPreparations[$preparat->getId()]['name'] = $preparat->getName();

                $PreparatInfoPackage = new PreparatInfoPackage;
                $options = [];
                $options['id_preparat'] = $preparat->getId();
                $options['only_active'] = false;

                $arPreparations[$preparat->getId()]['cntInfPack'] = $PreparatInfoPackage->getCntInfPackage($options);
            }//foreach

        } else if (array_intersect(['admin', 'director', 'product_manager'], $arRoles)) {
            $allPreparation = Preparation::findByActive(1);

            foreach ($allPreparation as $preparat) {
                $arPreparations[$preparat->getId()]['id'] = $preparat->getId();
                $arPreparations[$preparat->getId()]['name'] = $preparat->getName();

                $PreparatInfoPackage = new PreparatInfoPackage;
                $options = [];
                $options['id_preparat'] = $preparat->getId();
                $options['only_active'] = false;

                $arPreparations[$preparat->getId()]['cntInfPack'] = $PreparatInfoPackage->getCntInfPackage($options);
            }//foreach
        } else {
            $allPreparation = Preparation::findByActive(1);

            foreach ($allPreparation as $preparat) {
                $arPreparations[$preparat->getId()]['id'] = $preparat->getId();
                $arPreparations[$preparat->getId()]['name'] = $preparat->getName();

                $PreparatInfoPackage = new PreparatInfoPackage;
                $options = [];
                $options['id_preparat'] = $preparat->getId();
                $options['only_active'] = true;

                $arPreparations[$preparat->getId()]['cntInfPack'] = $PreparatInfoPackage->getCntInfPackage($options);
            }//foreach

        }//else

        asort($arPreparations);

        $lastInfoPackages = PreparatInfoPackage::find([
            'conditions' => 'active = 1',
            'order' => 'id DESC',
            'limit' => 10
        ]);

        $arLastInfoPackage = [];

        foreach ($lastInfoPackages as $value) {
            $arLastInfoPackage[$value->getId()]['id'] = $value->getId();
            $arLastInfoPackage[$value->getId()]['pip_name'] = $value->getName();
            $arLastInfoPackage[$value->getId()]['id_preparat'] = PreparatInfoPackagePreps::findFirstByIdPreparatInfoPackage($value->getId())->getIdPreparat();
        }//foreach

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'arPreparations' => $arPreparations,
            'arLastInfoPackage' => $arLastInfoPackage
        ), JSON_UNESCAPED_UNICODE);

    }

    public function getSpecAndRegAction()
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        /*Поиск всех специальностей*/
        $arSpecialty = [];
        $specialty = \Specialty::find();

        foreach ($specialty as $spec) {
            $arSpecialty[$spec->getId()] = $spec->getName();
        }//foreach

        /*Поиск всех доступных пользователю регионов*/
        $user = $this->userToken->Users;

        $arrRegionsUser = [];
        $userRegionsId = UsersRegions::findByid_user($user->id);

        foreach ($userRegionsId as $regionId) {
            $arrRegionsUser[] = $regionId->id_region;
        }

        $roles = $user->UsersRole;

        $arrRoles = [];
        foreach ($roles as $role) {
            $arrRoles[] = $role->Roles->name;
        }//foreach

        $arrRegions = [];

        if (in_array('director', $arrRoles) || in_array('admin', $arrRoles) || in_array('product_manager', $arrRoles) || in_array('trainer', $arrRoles)) {
            $regions = Regions::find(["order" => "name"]);
            $arrRegions[0] = '';
        }//if
        elseif (in_array('region_manager', $arrRoles)) {
            $regionModel = new Regions();
            $arrRegions[0] = '';
            $regions = $regionModel->getRegionsByArrId($arrRegionsUser);
        }//elseif
        else {
            $regionModel = new Regions();
            $regions = $regionModel->getRegionsByArrId($arrRegionsUser);
        }//elseif

        foreach ($regions as $region) {
            $arrRegions[$region->id] = $region->name;
        }//for

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'speciality' => $arSpecialty,
            'regions' => $arrRegions
        ), JSON_UNESCAPED_UNICODE);
    }

    /**Получение инфопакетов*/
    public function infoPackagesAction(int $preparatId)
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $curPreparat = Preparation::findFirstById($preparatId);

        $curUser = $this->userToken->Users;
        $roles = $curUser->UsersRole;

        $arRoles = [];
        foreach ($roles as $role) {
            $arRoles[] = $role->Roles->name;
        }//foreach

        if (!is_array($arRoles)) $arRoles = array($arRoles);

        $beginDate = $this->request->get('begin_date', 'int');
        $endDate = $this->request->get('end_date', 'int');
        $typeOa = $this->request->get('oa');
        $filterSpecialty = $this->request->get('specialty');

        if (!$curPreparat) {
            return json_encode(array('status' => 405, 'text' => 'Не удалось найти данные о препарате'), JSON_UNESCAPED_UNICODE);
        }//if

        $arInfoPackages = [];

        $preparatInfoPackageModel = new PreparatInfoPackage;
        $filter = ['begin_date' => $beginDate, 'end_date' => $endDate, 'type_oa' => $typeOa, 'specialty' => $filterSpecialty];
        $pipData = $preparatInfoPackageModel->getInfoPackages($preparatId, $filter);

        foreach ($pipData as $pipId => $pipItem) {

            if (!$pipItem['active'] && !array_intersect($this->editRoles, $arRoles)) {
                continue;
            }//if

            $arInfoPackages[$pipId]['name'] = $pipItem['preparat_info_package_name'];
            $arInfoPackages[$pipId]['description'] = $pipItem['description'];

            $text = empty($pipItem['preparat_info_package_text']) ? "Нет текста" : $pipItem['preparat_info_package_text'];
            $text = htmlspecialchars_decode(nl2br($text));
            $text = str_replace('[Имя ваше]', $curUser->getSurname() . " " . $curUser->getFirstName(), $text);

            $arInfoPackages[$pipId]['text'] = $text;
            $arInfoPackages[$pipId]['created'] = date("Y-m-d", $pipItem['created']);

            $oaName = $pipItem['oa_name'];

            $arInfoPackages[$pipId]['oa_name'] = ($oaName) ? $oaName : "Не указан";
            $arInfoPackages[$pipId]['active'] = ($pipItem['active']) ? "" : " danger ";

            $preparatInfoPackageSpecialty = PreparatInfoPackageSpecialty::findByIdPreparatInfoPackage($pipId);
            $arSpecialty = [];

            foreach ($preparatInfoPackageSpecialty as $pipSpecialty) {
                $specialty = Specialty::findFirstById($pipSpecialty->getIdSpecialty());

                if ($specialty) {
                    $arSpecialty[] = $specialty->getName();
                }//if

            }//foreach

            $arInfoPackages[$pipId]['specialty'] = count($arSpecialty) ? implode(", ", $arSpecialty) : "Не найдено";

            $pipPreps = PreparatInfoPackagePreps::findByIdPreparatInfoPackage($pipId);
            $arPreparats = [];

            foreach ($pipPreps as $pipPrep) {
                $preparat = Preparation::findFirstById($pipPrep->getIdPreparat());

                if ($preparat) {
                    $arPreparats[] = $preparat->getName();
                }//if

            }//foreach

            $arInfoPackages[$pipId]['preparats'] = count($arPreparats) ? implode(", ", $arPreparats) : "Не найдено";

            $arFiles = [];
            $appendPath = "/info_package_files/";
            $preparatInfoPackageFile = PreparatInfoPackageFile::findByIdPreparatInfoPackage($pipId);

            foreach ($preparatInfoPackageFile as $pipFile) {
                $arFiles["path"] = $appendPath . $pipFile->getPrefix() . "_" . $pipFile->getFileName();
                $arFiles["file_name"] = $pipFile->getFileName();
                $arInfoPackages[$pipId]['files'][] = $arFiles;
            }//foreach

            $arInfoPackages[$pipId]["share_title"] = $pipItem['preparat_info_package_name'];
            $arInfoPackages[$pipId]["share_description"] = $arInfoPackages[$pipId]['text'];

            $arInfoPackages[$pipId]["displayNoActive"] = "";

            if (!$pipItem['active']) {
                $arInfoPackages[$pipId]["displayNoActive"] = (!array_intersect($this->editRoles, $arRoles)) ? " d-none " : "";
            }//if

            $arInfoPackages[$pipId]['alert_viber_class'] = strlen($arInfoPackages[$pipId]['text']) < 200 ? " hidden " : "";
        }//foreach

        uasort($arInfoPackages, function ($arr1, $arr2) {
            $time1 = strtotime($arr1['created']);
            $time2 = strtotime($arr2['created']);

            if ($time1 == $time2)
                return 0;

            return ($time1 > $time2) ? -1 : 1;
        });

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'packages' => $arInfoPackages
        ), JSON_UNESCAPED_UNICODE);
    }

    /**Получение информации о пользователе*/
    public function UserInfoAction()
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $user = $this->userToken->Users;

        $direction = Direction::findFirstByid($user->id_direction);
        $roleList = [];

        $userRoleId = UsersRole::findByid_users($user->id);

        foreach ($userRoleId as $roleId) {
            $roleList[] = Roles::findFirstById($roleId->id_role);
        }

        $userRegions = [];
        $userRegionsId = UsersRegions::findByid_user($user->id);

        foreach ($userRegionsId as $regionId) {
            $region = Regions::findFirstByid($regionId->id_region);
            $userRegions[$region->id] = $region->name;
        }

        $create_date = date("d-m-Y", $user->date);

        $userInfo = [
            'first_name' => $user->first_name,
            'surname' => $user->surname,
            'patronymic' => $user->patronymic,
            'phone' => $user->phone,
            'email' => $user->email,
            'direction' => $direction,
            'role' => $roleList,
            'regions' => $userRegions,
            'create_date' => $create_date,
        ];

        return json_encode(array('status' => 200, 'text' => 'ok', 'userInfo' => $userInfo), JSON_UNESCAPED_UNICODE);
    }

    /**Смена пароля*/
    public function changePasswordAction()
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        if (!$this->request->isPost()) {
            return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        }//if

        $oldPass = $this->request->getPost('cur_password', 'string');
        $newPass = $this->request->getPost('new_password', 'string');
        $repeatPass = $this->request->getPost('repeat_password', 'string');

        //Проверка на длину
        if (strlen($oldPass) < 3 || strlen($newPass) < 3 || strlen($repeatPass) < 3) {
            return json_encode(array('status' => 406, 'text' => 'Слишком короткий пароль'), JSON_UNESCAPED_UNICODE);
        }

        $user = $this->userToken->Users;

        //Проверка текущего пароля
        if (!$this->security->checkHash($oldPass, $user->password)) {
            return json_encode(array('status' => 406, 'text' => 'Не верный текущий пароль!'), JSON_UNESCAPED_UNICODE);
        }

        //Проверка новых паролей
        if ($newPass !== $repeatPass) {
            return json_encode(array('status' => 406, 'text' => 'Введенные пароли не совпадают!'), JSON_UNESCAPED_UNICODE);
        }

        $user->setPassword($this->security->hash($newPass));

        if (!$user->update()) {
            return json_encode(array('status' => 406, 'text' => 'Не удалось изменить пароль. Попробуйте еще раз или обратитесь к администратору!'), JSON_UNESCAPED_UNICODE);
        }

        return json_encode(array('status' => 200, 'text' => 'Пароль успешно изменен!'), JSON_UNESCAPED_UNICODE);
    }

    /**Получение списка мероприятий*/
    public function listEventsAction()
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $curUser = $this->userToken->Users;

        $arOa = OutvisitActivity::getArAllValues();
        $arPreparations = Preparation::getArAllValues();

        $beginDate = $this->request->get('begin_date', 'int');
        $endDate = $this->request->get('end_date', 'int');
        $typeOa = $this->request->get('type_oa');
        $filterPreparat = $this->request->get('preparats');

        $weekDayName = array(1 => 'понедельник', 2 => 'вторник', 3 => 'среда', 4 => 'четверг', 5 => 'пятница', 6 => 'суббота', 0 => 'воскресенье');

        $eventModel = new Event;

        $filter = ['begin_date' => $beginDate, 'end_date' => $endDate, 'type_oa' => $typeOa, 'preparats' => $filterPreparat];
        $events = $eventModel->getEvents($filter);

        $arEvents = [];

        foreach ($events as $idEvent => $eventItem) {
            $arEvents[$idEvent]['name'] = $eventItem['event_name'];
            $arEvents[$idEvent]['date'] = $eventItem['event_date'];

            $text = htmlspecialchars_decode($eventItem['event_text']);
            $text = str_replace('[Имя ваше]', $curUser->getSurname() . " " . $curUser->getFirstName(), $text);

            $arEvents[$idEvent]['text'] = $text;
            $arEvents[$idEvent]['active'] = ($eventItem['active']) ? "" : " danger ";
            $arEvents[$idEvent]['oa_name'] = array_key_exists($eventItem['id_outvisit_activity'], $arOa) ? $arOa[$eventItem['id_outvisit_activity']] : "";

            $numDay = date("w", strtotime($eventItem['event_date']));
            $arEvents[$idEvent]['weekday'] = $weekDayName[$numDay];

            $eventPreparat = EventPreparat::findByIdEvent($idEvent);
            $arEp = [];

            foreach ($eventPreparat as $ePreparat) {
                $arEp[] = array_key_exists($ePreparat->getIdPreparat(), $arPreparations) ? $arPreparations[$ePreparat->getIdPreparat()] : "";
            }//foreach

            $arEvents[$idEvent]['preparations'] = implode(", ", $arEp);

            $arFiles = [];
            $appendPath = "/uploads/event_files/";
            $eventFile = EventFile::findByIdEvent($idEvent);

            foreach ($eventFile as $eFile) {
                $arFiles["path"] = $appendPath . $eFile->getPrefix() . "_" . $eFile->getFileName();
                $arFiles["file_name"] = $eFile->getFileName();
                $arEvents[$idEvent]['files'][] = $arFiles;
            }//foreach

            $arEvents[$idEvent]["share_title"] = $eventItem['event_name'];
            $arEvents[$idEvent]["share_description"] = strip_tags($arEvents[$idEvent]['text']);

            $arEvents[$idEvent]['alert_viber_class'] = strlen(strip_tags($arEvents[$idEvent]['text'])) < 200 ? " hidden " : "";
        }//foreach

        uasort($arEvents, function ($arr1, $arr2) {
            $time1 = strtotime($arr1['date']);
            $time2 = strtotime($arr2['date']);

            if ($time1 == $time2)
                return 0;

            return ($time1 > $time2) ? -1 : 1;
        });

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'data' => $arEvents
        ), JSON_UNESCAPED_UNICODE);
    }

    public function loginSubmitAction()
    {
        if (!$this->request->isPost()) {
            return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        }//if

        $mobileToken = $this->request->getPost("getToken");

        //login with database
        $login = $this->request->getPost("login");
        $password = $this->request->getPost("password");

        $user = Users::FindFirst(
            [
                'login= :login:',
                'bind' => [
                    'login' => $login,
                ]
            ]
        );

        if (!$user) {
            return json_encode(array('status' => 401, 'text' => 'Данный пользователь не зарегистрирован'), JSON_UNESCAPED_UNICODE);
        }//if

        //check Active user
        if (!$user->activation) {
            return json_encode(array('status' => 401, 'text' => 'Данный пользователь не активирован'), JSON_UNESCAPED_UNICODE);
        }//if

        if ($this->security->checkHash($password, $user->password)) {
            $userToken = UserTokens::findFirst('user_id = "' . $user->id . '" AND device_id = "' . $this->request->getPost("device_id") . '"');

            if (!$userToken) {
                $newUserToken = new UserTokens();
                $newUserToken->setUserId($user->id);
                $newUserToken->setDeviceId($this->request->getPost("device_id"));

            } else {
                $newUserToken = $userToken;
            }

            return $this->getToken($user->id, $newUserToken, $this->request->getPost("device_info"), $this->request->getPost("type_device"));
        }

        return json_encode(array('status' => 401, 'text' => 'Не удалось войти.'), JSON_UNESCAPED_UNICODE);
    }

    public function getDoctorsFromRegionAndSpecAction()
    {
        if (!$this->request->isPost()) {
            return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        }//if

        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $user = $this->userToken->Users;
        $userId = $user->id;

        $idObject = $this->request->getPost('idObject', 'int');
        $mpIds = $this->request->getPost('mpIds', 'int');
        $specId = $this->request->getPost('specIds', 'int');

        if (!is_array($mpIds) && !empty($mpIds)) $mpIds = array($mpIds);
        if (!is_array($specId) && !empty($specId)) $specId = array($specId);

        $data = [];
        $dataSent = [];

        $doctorModel = new Doctors();

        $shares = ShareTracker::find(
            [
                'conditions' => "id_user =  $userId AND id_object = $idObject",
            ]);

        $arrShares = [];

        foreach ($shares as $share) {
            $arrShares[$share->getIdDoctor()][$share->getTypeBtn()] = $share->getIdUser();
        }

        if (!empty($mpIds) && !empty($specId)) {
            $where = [];

            $str = implode(' OR mp_doctors.id_user = ', $mpIds);
            $where[] = " (mp_doctors.id_user = $str) ";

            $str = implode(' OR doctors.id_spec = ', $specId);
            $where[] = " (doctors.id_spec = $str) ";

            $queryStr = "WHERE " . implode(" AND ", $where);

            $sqlResoult = $doctorModel->getDoctorsFromSpecAndMp($queryStr);

            foreach ($sqlResoult as $item) {
                if (!isset($arrShares[$item->{'id_doctor'}])) {
                    $data[$item->{'id_doctor'}]['fio'] = $item->{'surname'} . ' ' . $item->{'first_name'} . ' ' . $item->{'patronymic'};
                    $data[$item->{'id_doctor'}]['email'] = $item->{'email'};
                    $data[$item->{'id_doctor'}]['phone'] = $item->{'phone'};
                    $data[$item->{'id_doctor'}]['io'] = $item->{'first_name'} . ' ' . $item->{'patronymic'};
                } else {
                    $dataSent[$item->{'id_doctor'}]['fio'] = $item->{'surname'} . ' ' . $item->{'first_name'} . ' ' . $item->{'patronymic'};
                    $dataSent[$item->{'id_doctor'}]['email'] = $item->{'email'};
                    $dataSent[$item->{'id_doctor'}]['phone'] = $item->{'phone'};
                    $dataSent[$item->{'id_doctor'}]['io'] = $item->{'first_name'} . ' ' . $item->{'patronymic'};

                    foreach ($arrShares[$item->{'id_doctor'}] as $type => $idUser) {
                        if ($type == "Скопировать текст в буфер обмена")
                            $dataSent[$item->{'id_doctor'}]['sent'][] = "copy";
                        else
                            $dataSent[$item->{'id_doctor'}]['sent'][] = $type;
                    }
                }
            }

        }


        asort($data);
        asort($dataSent);

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'data' => $data + $dataSent
        ), JSON_UNESCAPED_UNICODE);
    }

    public function sendTrackingInfoAction()
    {

        if (!$this->request->isPost()) {
            return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        }//if

        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);


        $user = $this->userToken->Users;
        $idUser = $user->id;

        $deviceInfo = $this->request->getPost('deviceInfo', 'string');


        $typeBtn = $this->request->getPost('typeBtn', 'string');
        $curDatetime = date("Y-m-d H:i:s");
        $typeDevice = $this->request->getPost('typeDevice', 'string');
        $idObject = $this->request->getPost('idObject', 'int');
        $typeObject = $this->request->getPost('typeObject', 'string');
        $idDoctor = $this->request->getPost('idDoctor', 'int');
        $screenResolution = $this->request->getPost('screenResolution', 'string');

        $ShareTracker = new ShareTracker();

        $ShareTracker->setIdUser($idUser);
        $ShareTracker->setTypeBtn($typeBtn);
        $ShareTracker->setDate($curDatetime);
        $ShareTracker->setTypeDevice($typeDevice);
        $ShareTracker->setIdObject($idObject);
        $ShareTracker->setTypeObject($typeObject);
        $ShareTracker->setIdDoctor($idDoctor);
        $ShareTracker->setDeviceInfo($deviceInfo);
        $ShareTracker->setScreenResolution($screenResolution);

        if ($ShareTracker->save()) {
            return json_encode(array(
                'status' => 200,
                'text' => 'ok'
            ), JSON_UNESCAPED_UNICODE);
        } else {
            return json_encode(array(
                'status' => 300,
                'text' => 'Не удалось отметить отправку'
            ), JSON_UNESCAPED_UNICODE);
        }//else
    }

    /**Получение списка планов/отчетов*/
    public function listPlansAction()
    {
        if (!$this->request->isPost()) {
            return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        }//if

        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $user = $this->userToken->Users;
        $idUser = $user->id;

        $this->session->set('AUTH_ID', $idUser);

        $PlansObj = new Plans();
        $arrPlansObj = $PlansObj->getMyListDate(true);

        $arrPlans = [];
        foreach ($arrPlansObj as $plan) {
            $user = Users::findFirstById($plan->id_user);
            $arrPlans[$plan->id]['user'] = "$user->surname $user->first_name $user->patronymic";
            $arrPlans[$plan->id]['note'] = !empty($plan->note) ? $plan->note : "пусто";

            $arrPlans[$plan->id]['dirVisit'] = $plan->dirVisit;
            $arrPlans[$plan->id]['date'] = $plan->date;
            $var = date("w", strtotime($plan->date));
            $dateWeek = array(1 => 'понедельник', 2 => 'вторник', 3 => 'среда', 4 => 'четверг', 5 => 'пятница', 6 => 'суббота', 0 => 'воскресенье');
            $arrPlans[$plan->id]['dateWeek'] = $dateWeek[$var];
        }

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'arrPlans' => $arrPlans
        ), JSON_UNESCAPED_UNICODE);
    }

    /** Поиск докторов*/
    public function searchDoctorsAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);

        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $user = $this->userToken->Users;
        $idUser = $user->id;

        $this->session->set('AUTH_ID', $idUser);

        $arrRegionsId = [];
        $userRegionsId = UsersRegions::findByid_user($idUser);
        foreach ($userRegionsId as $regionId) {
            $arrRegionsId[] = $regionId->id_region;
        }

        $arrRoles = [];
        $roles = $user->UsersRole;
        foreach ($roles as $role) {
            $arrRoles[] = $role->Roles->name;
        }//foreach

        if (!(!is_null($arrRegionsId) || in_array('admin', $arrRoles) || in_array('director', $arrRoles) || in_array('product_manager', $arrRoles)))
            return json_encode(array('status' => 405, 'text' => 'Ошибка'), JSON_UNESCAPED_UNICODE);

//        $plansIdentification = $this->request->getPost('plansIdentification', 'string');
//        $docRegionId = $this->request->getPost('region');
//        $singleSearch = $this->request->getPost('singleSearch', 'trim');
//        $mpId = $this->request->getPost('med_representative');
//        $docActive = $this->request->getPost('active');
//        $docOl = $this->request->getPost('ol');
//        $hospitalsId = $this->request->getPost('hospital');
//        $docSpecialtyId = $this->request->getPost('specialty');
//        $postId = $this->request->getPost('post', 'int');
//        $searchAll = $this->request->getPost('searchAll', 'string');
//        $beginDate = $this->request->getPost('beginDate', 'int');
//        $endDate = $this->request->getPost('endDate', 'int');
//        $segmIMS = $this->request->getPost('segmIMS', 'string');
//        $phone = $this->request->getPost('phone', 'string');
//        $email = $this->request->getPost('email', 'string');
//        $attached = $this->request->getPost('attached', 'string');
//        $testingResource = $this->request->getPost('testingResource', 'int');
//        $segmentPotential = $this->request->getPost('segmentPotential');
//        $segmentLoyalty = $this->request->getPost('segmentLoyalty');
//        $visitsFrom = $this->request->getPost('visitsFrom', 'int');
//        $visitsBefore = $this->request->getPost('visitsBefore', 'int');
//        $medications = $this->request->getPost('medications');
//        $lpu = $this->request->getPost('lpus');

//        $options = ['arr_regions_id' => $arrRegionsId, 'arr_roles' => $arrRoles, 'id_user' => $idUser,
//            'plans_identification' => $plansIdentification, 'doc_region_id' => $docRegionId,
//            'single_search' => $singleSearch, 'mp_id' => $mpId, 'doc_active' => $docActive,
//            'doc_ol' => $docOl, 'hospitals_id' => $hospitalsId, 'doc_specialty_id' => $docSpecialtyId,
//            'post_id' => $postId, 'search_all' => $searchAll, 'begin_date' => $beginDate,
//            'end_date' => $endDate, 'segm_IMS' => $segmIMS, 'phone' => $phone, 'email' => $email,
//            'attached' => $attached, 'testing_resource' => $testingResource,
//            'segment_potential' => $segmentPotential, 'segment_loyalty' => $segmentLoyalty, 'visitsFrom' => $visitsFrom,
//            'visitsBefore' => "$visitsBefore", 'medications' => $medications , "lpu" => $lpu
//        ];

        $searchAll = $this->request->getPost('searchAll');
        $active = $this->request->getPost('active');

        $options = ['arr_regions_id' => $arrRegionsId, 'arr_roles' => $arrRoles, 'id_user' => $idUser,
            'visitsFrom' => "",
            'visitsBefore' => "",
            'search_all' => $searchAll,
            'region' => $arrRegionsId[0],
            'doc_active' => $active
        ];

        $doctorsModel = new Doctors();
        $arrDoctors = [];

        $countDoc = $doctorsModel->getCountDoc($options);

        $start = $this->request->getPost('start');

        if ($countDoc && $start < $countDoc) {

            $cntOnPage = $this->request->getPost('countOnPage');

            $options["start"] = $start;
            $options["cnt_on_page"] = $cntOnPage;

            $doctorsList = $doctorsModel->getIdDoctors($options);

            $arrRegions = [];
            $arrRegions[0] = "Не определен";
            $regions = Regions::find();
            foreach ($regions as $r) {
                $arrRegions[$r->id] = $r->getName();
            }//foreach
            unset($regions);

            $posts = Posts::find();
            $arrPosts = [];
            foreach ($posts as $p) {
                $arrPosts[$p->id] = $p->getName();
            }//foreach
            unset($posts);

            $specialty = Specialty::find();
            $arrSpecialty = [];
            $arrSpecialty[0] = "Не определена";
            foreach ($specialty as $spec) {
                $arrSpecialty[$spec->id] = $spec->getName();
            }//foreach

            /*Поиск ЛПУ*/
            $arrDocId = [];
            $currentDocList = $doctorsList;
            foreach ($currentDocList as $doctor) {
                $arrDocId[] = $doctor->id;
            }
            $doctorLpuModel = new DoctorsLpu();
            $parameters['docId'] = $arrDocId;

            $arrLpuId = $doctorLpuModel->findLpuByIds($parameters);

            unset($arrDocId);
            unset($parameters);
            unset($doctorLpuModel);

            $arrLpu = [];
            foreach ($arrLpuId as $item) {
                $lpu = Lpu::findFirstByid($item['id_lpu']);

                if ($lpu) {
                    if (!isset($arrLpu[$item['id_doctor']])) {
                        $arrLpu[$item['id_doctor']] = $lpu->name . " ($lpu->adres)";
                    } else {
                        $arrLpu[$item['id_doctor']] .= ", " . $lpu->name . " ($lpu->adres)";
                    }
                }
            }

            foreach ($doctorsList as $doctor) {
                $arrDoctors[$doctor->id]['fio'] = "$doctor->surname $doctor->first_name $doctor->patronymic";

                $arrDoctors[$doctor->id]['ov'] = $doctorsModel->getLastVisitDateDoc($doctor->id, in_array('med_representative', $arrRoles));;
                $arrDoctors[$doctor->id]['dv'] = $doctorsModel->getLastDistVisitDateDoc($doctor->id, in_array('med_representative', $arrRoles));

                $arrDoctors[$doctor->id]['countVisits'] = $doctorsModel->getCountVisitOneDocByUser($doctor->id, in_array('med_representative', $arrRoles));
                $arrDoctors[$doctor->id]['specName'] = $arrSpecialty[$doctor->id_spec];

                /*Поиск организации*/
                $doctorHospital = DoctorHospitals::findByIdDoctor($doctor->id);
                $organization = [];

                foreach ($doctorHospital as $dh) {
                    $organization[] = Hospitals::findFirstById($dh->getIdHospital())->getName();
                }

                $arrDoctors[$doctor->id]['organization'] = implode(', ', $organization);
                $arrDoctors[$doctor->id]['regionName'] = $arrRegions[$doctor->id_region];
                $arrDoctors[$doctor->id]['city'] = $doctor->city;
                $arrDoctors[$doctor->id]['lpu'] = $arrLpu[$doctor->id] ?? "";
                $arrDoctors[$doctor->id]['post'] = $arrPosts[$doctor->id_post];
                $arrDoctors[$doctor->id]['email'] = $doctor->email;
                $arrDoctors[$doctor->id]['phone'] = $doctor->phone;
                $arrDoctors[$doctor->id]['city_phone'] = $doctor->city_phone;

                $arrDoctors[$doctor->id]['ol'] = $doctor->ol ? "Да" : "Нет";
                $arrDoctors[$doctor->id]['IMS'] = $doctor->ims_2019 ? "Да" : "Нет";
                $arrDoctors[$doctor->id]['isActive'] = $doctor->active ? "Да" : "Нет";
                $arrDoctors[$doctor->id]['birthday'] = !empty($doctor->bithday) && $doctor->bithday !== "NULL" ? $doctor->bithday : "";
                $arrDoctors[$doctor->id]['note'] = !empty($doctor->note) ? $doctor->note : "Пусто";
            }
        }


        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'doctors' => $arrDoctors
        ), JSON_UNESCAPED_UNICODE);
    }

    /** Поиск аптек*/
    public function searchPharmacyAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $user = $this->userToken->Users;
        $idUser = $user->id;

        $this->session->set('AUTH_ID', $idUser);

        $arrRegionsId = [];
        $userRegionsId = UsersRegions::findByid_user($idUser);
        foreach ($userRegionsId as $regionId) {
            $arrRegionsId[] = $regionId->id_region;
        }

        $arrRoles = [];
        $roles = $user->UsersRole;
        foreach ($roles as $role) {
            $arrRoles[] = $role->Roles->name;
        }//foreach

        if (!(!is_null($arrRegionsId) || in_array('admin', $arrRoles) || in_array('director', $arrRoles) || in_array('product_manager', $arrRoles)))
            return json_encode(array('status' => 405, 'text' => 'Ошибка'), JSON_UNESCAPED_UNICODE);

        $where = [];

        $pharmacyRegionId = $this->request->getPost('region');
        if (!empty($pharmacyRegionId)) {
            $where[] = " Pharmacy.id_region =  $pharmacyRegionId";
        }//if
        elseif (empty($pharmacyRegionId)) {
            $str = implode(' OR Pharmacy.id_region = ', $arrRegionsId);
            $where[] = " (Pharmacy.id_region = $str) ";
        }//elseif

        $singleSearch = $this->request->getPost('singleSearch');
        if (!empty($singleSearch)) {
            $where[] = " (Pharmacy.name LIKE '%$singleSearch%' OR Pharmacy.surname LIKE '%$singleSearch%' OR Pharmacy.address LIKE '%$singleSearch%' OR Pharmacy.city LIKE '%$singleSearch%' OR Pharmacy.note LIKE '%$singleSearch%') ";
        }//if

        $mpId = $this->request->getPost('med_representative');
        $searchAll = $this->request->getPost('searchAll');

        if (!empty($mpId) && (empty($searchAll) || $searchAll == "true")) {
            $where[] = " MpPharmacy.id_user = $mpId ";
        }//if

        $pharmActive = $this->request->getPost('active');
        if ($pharmActive != '') {
            $where[] = "  Pharmacy.active = $pharmActive";
        }

        if ($searchAll == 'true') {
            $where[] = "MpPharmacy.id_user = $idUser";
        }//if

        $category = $this->request->getPost('category');
        if (!empty($category)) {
            $where[] = " Pharmacy.category = '$category' ";
        }//if

        $beginDate = $this->request->getPost('beginDate');
        if (!empty($beginDate)) {
            $where[] = "  Plans.date >= '$beginDate' ";
        }//if

        $endDate = $this->request->getPost('endDate');
        if (!empty($endDate)) {
            $where[] = "  Plans.date <= '$endDate' ";
        }//if

        if (!empty($beginDate) || !empty($endDate)) {
            $where[] = " ObjectInPlans.type_object = 'pharmacy' AND Plans.report = 1 ";
        }//if

        $networking = $this->request->getPost('networking');
        if ($networking != '') {
            $where[] = " Pharmacy.networking = $networking ";
        }//if

        $contract = $this->request->getPost('contract');
        if ($contract != '') {
            $where[] = " Pharmacy.contract = $contract ";
        }//if

        //формируем строку запроса
        $whereQueryStr = implode(' AND ', $where);
        if (!empty($whereQueryStr)) {
            $whereQueryStr = "WHERE " . $whereQueryStr;
        }//if

        $PharmacyModel = new Pharmacy();

        $countPharm = $PharmacyModel->getCountPharm($whereQueryStr);
        $start = $this->request->getPost('start');
        $arrPharmacy = [];

        if ($countPharm && $start < $countPharm) {

            $cntOnPage = $this->request->getPost('countOnPage');

            $whereQueryStr .= " LIMIT $start,$cntOnPage ";

            $pharmacyList = $PharmacyModel->getIdPharmacy($whereQueryStr);

            foreach ($pharmacyList as $pharmacy) {
                $arrPharmacy[$pharmacy->id]['id'] = $pharmacy->id;
                $arrPharmacy[$pharmacy->id]['name'] = $pharmacy->name;
                $arrPharmacy[$pharmacy->id]['fio'] = "$pharmacy->surname $pharmacy->first_name $pharmacy->patronymic";
                $post = Posts::findFirstById($pharmacy->id_post);
                $arrPharmacy[$pharmacy->id]['post'] = $post->name;
                $arrPharmacy[$pharmacy->id]['lastVisit'] = implode(', ', (array)$PharmacyModel->getLastVisitDatePharm($pharmacy->id, in_array('med_representative', $arrRoles)));
                $arrPharmacy[$pharmacy->id]['countVisits'] = $PharmacyModel->getCountVisitOnePharmByUser($pharmacy->id, in_array('med_representative', $arrRoles));
                $arrPharmacy[$pharmacy->id]['isNetworking'] = $pharmacy->networking ? "Да" : "Нет";
                $contract = $pharmacy->contract;
                $contractName = "нет";
                if ($contract) {
                    $PN = PharmacyNetwork::findFirstById($contract);
                    $contractName = $PN->getName();
                }
                $arrPharmacy[$pharmacy->id]['contract'] = $contractName;
                $region = Regions::findFirstByid($pharmacy->id_region);
                $regionName = "не определен";
                if ($region) {
                    $regionName = $region->getName();
                }
                $arrPharmacy[$pharmacy->id]['regionName'] = $regionName;

                $arrPharmacy[$pharmacy->id]['city'] = $pharmacy->city;
                $arrPharmacy[$pharmacy->id]['address'] = $pharmacy->address;
                $arrPharmacy[$pharmacy->id]['email'] = $pharmacy->email;
                $arrPharmacy[$pharmacy->id]['phone'] = $pharmacy->phone;
                $arrPharmacy[$pharmacy->id]['isActive'] = $pharmacy->active ? "да" : "нет";
                $arrPharmacy[$pharmacy->id]['category'] = $pharmacy->category;
                $arrPharmacy[$pharmacy->id]['note'] = $pharmacy->note;
            }
        }

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'pharmacy' => $arrPharmacy
        ), JSON_UNESCAPED_UNICODE);
    }

    /** Удаление плана*/
    public function deletePlanAction()
    {
        if (!$this->request->isPost()) {
            return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        }

        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $planId = $this->request->getPost('planId', 'int');

        $currentPlan = Plans::findFirstById($planId);
        if (!$currentPlan) {
            return json_encode(array('status' => 404, 'text' => 'Запись не найдена'), JSON_UNESCAPED_UNICODE);
        }

        if (!$currentPlan->delete()) {
            foreach ($currentPlan->getMessages() as $message) {
                return json_encode(array('status' => 404, 'text' => $message), JSON_UNESCAPED_UNICODE);
            }
        }

        return json_encode(array('status' => 200, 'text' => 'План удален'), JSON_UNESCAPED_UNICODE);
    }

    /** Создание нового плана*/
    public function createPlanAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $dateReport = $this->request->getPost('dateReport', 'string');

        $date = new \DateTime($dateReport);
        $dateReport = $date->format('Y-m-d');

        $beginVacationDate = $this->request->getPost('beginVacationDate', 'string');
        $endVacationDate = $this->request->getPost('endVacationDate', 'string');
        $outvisitActivity = $this->request->getPost('outvisitActivity', 'int');
        $note = $this->request->getPost('note', 'string');
        $arrDocId = $this->request->getPost('arrDocId', 'string');
        $arrPharmId = $this->request->getPost('arrPharmId', 'string');

        $user = $this->userToken->Users;
        $usrId = $user->id;

        $arrDocInExistPlan = [];
        $arrPharmInExistPlan = [];

        if (!empty($dateReport) && $outvisitActivity != 10 && $outvisitActivity != 11 && $outvisitActivity != 15) {

            $oa = OutvisitActivity::findFirst("id = " . $outvisitActivity);
            $is_work = $oa->getIsWork();

            $isPlanExist = false;
            $userPlans = Plans::find("id_user = $usrId AND date = '$dateReport'");
            foreach ($userPlans as $userPlan) {
                $oa_tmp = OutvisitActivity::findFirst("id = " . $userPlan->getActivityId());
                if ($oa_tmp->getIsWork()) {
                    $isPlanExist = $userPlan;
                    break;
                }
            }
            //start transaction
            $this->db->begin();
            if ($isPlanExist && $is_work) {
                $Plans = $isPlanExist;
                $objInPlans = ObjectInPlans::find("id_plan = $Plans->id");
                foreach ($objInPlans as $obj) {
                    if ($obj->getTypeObject() == 'doctor') {
                        $arrDocInExistPlan[] = $obj->getIdObject();
                    }//if
                    elseif ($obj->getTypeObject() == 'pharmacy') {
                        $arrPharmInExistPlan[] = $obj->getIdObject();
                    }//else
                }//foreach
            }//if
            else {
                $Plans = new Plans();
            }//else

            $Plans->setDate($dateReport);
            $Plans->setActivityId($outvisitActivity);
            $Plans->setIdUser($usrId);
            $Plans->setReport(0);
            $Plans->setNote($note);

            if (!$Plans->save()) {
                foreach ($Plans->getMessages() as $message) {
                    $this->db->rollback();
                    return json_encode(array('status' => 404, 'text' => $message), JSON_UNESCAPED_UNICODE);
                }//foreach
            }//if

            //save doctors
            if (!empty($arrDocId)) {
                foreach ($arrDocId as $idDoc) {
                    if (in_array($idDoc, $arrDocInExistPlan)) continue;
                    $ObjectInPlans = new ObjectInPlans();
                    $ObjectInPlans->setIdPlan($Plans->getId());
                    $ObjectInPlans->setIdObject((int)$idDoc);
                    $ObjectInPlans->setTypeObject('doctor');

                    if (!$ObjectInPlans->save()) {
                        foreach ($ObjectInPlans->getMessages() as $message) {
                            $this->db->rollback();
                            return json_encode(array('status' => 404, 'text' => $message), JSON_UNESCAPED_UNICODE);
                        }//foreach
                    }//if
                }//foreach
            }//if

            //save pharmacy
            if (!empty($arrPharmId)) {
                foreach ($arrPharmId as $pharmId) {
                    if (in_array($pharmId, $arrPharmInExistPlan)) continue;
                    $ObjectInPlans = new ObjectInPlans();
                    $ObjectInPlans->setIdPlan($Plans->getId());
                    $ObjectInPlans->setIdObject((int)$pharmId);
                    $ObjectInPlans->setTypeObject('pharmacy');

                    if (!$ObjectInPlans->save()) {
                        foreach ($ObjectInPlans->getMessages() as $message) {
                            $this->db->rollback();
                            return json_encode(array('status' => 404, 'text' => $message), JSON_UNESCAPED_UNICODE);
                        }//foreach
                    }//if
                }//foreach
            }//if

            $this->db->commit();
            return json_encode(array('status' => 200, 'text' => 'План создан'), JSON_UNESCAPED_UNICODE);
        } elseif (!empty($beginVacationDate) && !empty($endVacationDate) && ($outvisitActivity == 10 || $outvisitActivity == 11 || $outvisitActivity == 15)) {
            $beginDate = strtotime($beginVacationDate);
            $endDate = strtotime($endVacationDate);
            $dateDiff = $endDate - $beginDate;

            if ($dateDiff < 0) {
                return json_encode(array('status' => 404, 'text' => 'Ошибка! Дата начала превышает дату окончания'), JSON_UNESCAPED_UNICODE);
            }

            $countDay = floor($dateDiff / (60 * 60 * 24)) + 1;
            $arrDays[] = $beginVacationDate;
            for ($i = 1; $i < $countDay; $i++) {
                $arrDays[] = date('Y-m-d', ($beginDate + ($i * 60 * 60 * 24)));
            }

            $this->db->begin();

            foreach ($arrDays as $day) {
                $isExistPlans = Plans::findFirst("id_user = $usrId AND date = '$day'");
                if ($isExistPlans) {
                    $this->flashSession->error("Ошибка, на дату $day уже существует план!");
                    $this->response->redirect('plans/addPlans');
                    return;
                }//if
                $PlansObj = new Plans();
                $PlansObj->setDate($day);
                $PlansObj->setActivityId($outvisitActivity);
                $PlansObj->setIdUser($usrId);
                $PlansObj->setReport(0);
                $PlansObj->setNote($note);

                if (!$PlansObj->save()) {
                    foreach ($PlansObj->getMessages() as $message) {
                        $this->db->rollback();
                        return json_encode(array('status' => 404, 'text' => $message), JSON_UNESCAPED_UNICODE);
                    }
                }
            }

            $this->db->commit();
            return json_encode(array('status' => 200, 'text' => 'План создан'), JSON_UNESCAPED_UNICODE);
        } else {
            return json_encode(array('status' => 402, 'text' => "Ошибка"), JSON_UNESCAPED_UNICODE);
        }
    }

    /** Закрепление/Открепление Доктора за пользователем*/
    public function attachDoctorByUserAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $docId = $this->request->getPost('docId', 'int');

        $user = $this->userToken->Users;
        $userId = $user->id;

        $MpDoctors = new MpDoctors();

        $recordAboutMp = $MpDoctors->getRecordMpDoctors($userId, $docId);

        if (count($recordAboutMp)) {
            $MpDoctorsHistory = new MpDoctorsHistory();
            $MpDoctorsHistory->setIdUser($userId);
            $MpDoctorsHistory->setIdDoctor($docId);
            $MpDoctorsHistory->setAttachDate($recordAboutMp[0]->getDate());
            $MpDoctorsHistory->setDetachDate(date("Y-m-d"));
            $MpDoctorsHistory->save();
            $recordAboutMp->delete();
        } else {
            $MpDoctors->setDate(date("Y-m-d"));
            $MpDoctors->setIdDoctor($docId);
            $MpDoctors->setIdUser($userId);
            $MpDoctors->save();
        }

        $doctor = Doctors::findFirstById($docId);
        $doctor->setUpdated(time());
        $doctor->save();

        return json_encode(array('status' => 200, 'text' => 'ok'), JSON_UNESCAPED_UNICODE);
    }

    /** Закрелпение/Открепление Аптеки за пользователем*/
    public function attachPharmacyByUserAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $pharmacyId = $this->request->getPost('pharmacyId', 'int');

        $user = $this->userToken->Users;
        $userId = $user->id;

        $MpPharmacy = new MpPharmacy();

        $recordAboutMp = $MpPharmacy->getRecordMpPharmacy($userId, $pharmacyId);

        if (count($recordAboutMp)) {
            $recordAboutMp->delete();
        } else {
            $MpPharmacy->setIdPharmacy($pharmacyId);
            $MpPharmacy->setIdUser($userId);
            $MpPharmacy->save();
        }

        return json_encode(array('status' => 200, 'text' => 'ok'), JSON_UNESCAPED_UNICODE);
    }

    /** Виды активности при создании нового плана*/
    public function getActivityDataAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $activities = OutvisitActivity::find();
        $arrActivity = [];

        foreach ($activities as $activity) {
            $arDisplayPlace = explode(',', $activity->getDisplayPlace());

            if (in_array('plans', $arDisplayPlace)) {
                $arrActivity[$activity->id]['id'] = $activity->id;
                $arrActivity[$activity->id]['name'] = $activity->name;
            }
        }

        return json_encode(array('status' => 200, 'text' => 'ok', 'data' => $arrActivity), JSON_UNESCAPED_UNICODE);
    }

    /** Получает список докторов для заполнения лояльности*/
    public function getDoctorsSelectForLoyaltyAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $user = $this->userToken->Users;
        $idUser = $user->id;

        $planId = $this->request->getPost('planId', 'int');
        $idDirection = $user->getIdDirection();

        $segmentModel = new Segment();
        $doctors = $segmentModel->getDoctorsForLoyaltyForm((int)$planId, (int)$idDirection);

        $result = [];
        $doctorsModel = new Doctors();

        foreach ($doctors as $doctor) {
            /*ST заданный в ручную*/
//            $isSt = $doctorsModel->isArtificialSt($doctor->id, $idUser);
//
//            if(!$isSt) {
//                $potentialId = $doctorsModel->getPotentialId($doctor->id, $idUser);
//                $isSt = $doctorsModel->isST($doctor->id, $potentialId, (int)$idDirection, $idUser);
//            }
//
//            $countVisits = $doctorsModel->getCountVisitOneDocByUser($doctor->id, true);
//
//            $divisor = $isSt ? 6 : 3;
//            if(($countVisits + 1) % $divisor == 0) {
//                $result[] = $doctor;
//            }

            $result[$doctor->id]['id'] = $doctor->id;
            $result[$doctor->id]['name'] = $doctor->fio;
        }

        return json_encode(array('status' => 200, 'text' => 'ok', 'data' => $result), JSON_UNESCAPED_UNICODE);
    }

    /** Получить данные по форме лояльности доктора*/
    public function getSegmentLoyaltyFormDataAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $idDoctor = $this->request->getPost('idDoctor', 'int');

        $doctor = Doctors::findFirstById($idDoctor);

        if (!$doctor) {
            return json_encode(array('status' => 404, 'text' => 'Данные по всем докторам заполнены!'), JSON_UNESCAPED_UNICODE);
        }

        $LoyaltyData = [];
        $i = 0;

        /*Текущие роли пользователя*/
        $user = $this->userToken->Users;
        $arrRoles = [];
        $roles = $user->UsersRole;
        foreach ($roles as $role) {
            $arrRoles[] = $role->Roles->id;
        }

        $idSpecialty = $doctor->getIdSpec();
        $arDirections = Direction::getArAllValues();
        $segmentModel = new Segment();

        /*Проверка на МП*/
        if ($arrRoles[0] == 7 || $arrRoles[0] == 8) {
            $idDirection = $user->getIdDirection();

            $filter = ['id_direction' => $idDirection, 'id_specialty' => $idSpecialty];
            $questions = $segmentModel->getLoyaltyQuestions($filter);
            $options = $segmentModel->getSegmentPrescripeOptions($filter);

            //количество вопросов для каждого препарата
//            $arCntPreparatQuestion = [];

//            foreach ($questions as $q) {
//                if (isset($arCntPreparatQuestion[$q['preparat_name']])) {
//                    $arCntPreparatQuestion[$q['preparat_name']]++;
//                } else {
//                    $arCntPreparatQuestion[$q['preparat_name']] = 1;
//                }
//            }

            //Выбранные доп. опции
            $selectedOption = [];
            //Выбранные вопросы
            $selectedQuestion = [];

            $segmentPrescribeValue = SegmentPrescribeValue::findByIdDoctor($idDoctor);

            foreach ($segmentPrescribeValue as $s) {
                $idSegmentPrescribe = $s->getIdSegmentPrescribe();
                $selectedQuestion[] = $idSegmentPrescribe;

                $segmentPrescribeOptionValue = SegmentPrescribeOptionValue::find("
				id_doctor = $idDoctor AND
				id_segment_prescribe = $idSegmentPrescribe
			");

                foreach ($segmentPrescribeOptionValue as $so) {
                    $selectedOption[$idSegmentPrescribe][] = $so->getIdSegmentPrescribeOption();
                }//foreach
            }//foreach

            $LoyaltyData[$i]['questions'] = $questions;
            $LoyaltyData[$i]['options'] = $options;

            $LoyaltyData[$i]['selectedQuestion'] = $selectedQuestion;
            $LoyaltyData[$i]['selectedOption'] = $selectedOption;

//            $LoyaltyData[$i]['arCntPreparatQuestion'] = $arCntPreparatQuestion;
//            $LoyaltyData[$i]['direction'] = (array_key_exists($idDirection, $arDirections) ? $arDirections[$idDirection] : '');
            $LoyaltyData[$i]['id'] = $i;
        } else {
            //Получение списка лояльности

            $ListSegmentLoyaltyDoctorDirection = $this->segmentLoyaltyDoctorDirectionModel->findUnique($idDoctor);

            if (count($ListSegmentLoyaltyDoctorDirection) == 0) {
                $message = "Данные по лояльности не обнаружены";
                return "<div class='alert alert-info'>" . $message . "</div>";
            }

            foreach ($ListSegmentLoyaltyDoctorDirection as $item) {
                $idDirection = $item->{'id_direction'};
                $filter = ['id_direction' => $idDirection, 'id_specialty' => $idSpecialty];
                $questions = $this->segmentModel->getLoyaltyQuestions($filter);
                $options = $this->segmentModel->getSegmentPrescripeOptions($filter);

                //количество вопросов для каждого препарата
                $arCntPreparatQuestion = [];

                foreach ($questions as $q) {

                    if (isset($arCntPreparatQuestion[$q['preparat_name']])) {
                        $arCntPreparatQuestion[$q['preparat_name']]++;
                    } else {
                        $arCntPreparatQuestion[$q['preparat_name']] = 1;
                    }//else

                }//foreach

                //Выбранные доп. опции
                $selectedOption = [];
                //Выбранные вопросы
                $selectedQuestion = [];

                $segmentPrescribeValue = SegmentPrescribeValue::findByIdDoctor($idDoctor);

                foreach ($segmentPrescribeValue as $s) {
                    $idSegmentPrescribe = $s->getIdSegmentPrescribe();
                    $selectedQuestion[] = $idSegmentPrescribe;

                    $segmentPrescribeOptionValue = SegmentPrescribeOptionValue::find("
				id_doctor = $idDoctor AND
				id_segment_prescribe = $idSegmentPrescribe
			");

                    foreach ($segmentPrescribeOptionValue as $so) {
                        $selectedOption[$idSegmentPrescribe][] = $so->getIdSegmentPrescribeOption();
                    }//foreach
                }//foreach

                $LoyaltyData[$i]['questions'] = $questions;
                $LoyaltyData[$i]['options'] = $options;
                $LoyaltyData[$i]['arCntPreparatQuestion'] = $arCntPreparatQuestion;
                $LoyaltyData[$i]['selectedQuestion'] = $selectedQuestion;
                $LoyaltyData[$i]['selectedOption'] = $selectedOption;
                $LoyaltyData[$i]['direction'] = (array_key_exists($idDirection, $arDirections) ? $arDirections[$idDirection] : '');
                $LoyaltyData[$i]['id'] = $i;
                $i++;
            }

        }

        $result = [];

        foreach ($LoyaltyData as $id => $item) {

            foreach ($item['questions'] as $q) {

                if (!isset($result[$id][$q['preparat_name']]['id'])) {
                    $result[$id][$q['preparat_name']]['id'] = $q['preparat_name'];
                }

                $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['text'] = $q['text_prescribe'];
                $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['value'] = $q['weight'];

                if (in_array($q['id_segment_prescribe'], $item['selectedQuestion'])) {
                    $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['isChecked'] = true;
                } else {
                    $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['isChecked'] = false;
                }

                foreach ($item['options'] as $idSegmentPrescribeOption => $value) {
                    if (isset($value[$q['id_segment_preparat_prescribe']])) {
                        $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['options'][$idSegmentPrescribeOption]['text'] = $value[$q['id_segment_preparat_prescribe']];
                        $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['options'][$idSegmentPrescribeOption]['value'] = $idSegmentPrescribeOption;

                        if (isset($item['selectedOption'][$q['id_segment_prescribe']]) &&
                            in_array($idSegmentPrescribeOption, $item['selectedOption'][$q['id_segment_prescribe']])) {
                            $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['options'][$idSegmentPrescribeOption]['isChecked'] = true;
                        } else {
                            $result[$id][$q['preparat_name']]['questions'][$q['id_segment_prescribe']]['options'][$idSegmentPrescribeOption]['isChecked'] = false;
                        }
                    }
                }

            }
        }

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'specialtyName' => (Specialty::findFirst("id = $idSpecialty"))->getName(),
            'loyaltyData' => $result
        ), JSON_UNESCAPED_UNICODE);
    }

    /** Получение данных о докторе*/
    public function getDoctorDataAction(int $doctorId)
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $data = [];

        $doctor = Doctors::findFirstByid($doctorId);

        if ($doctor) {
            $data['surname'] = $doctor->surname;
            $data['name'] = $doctor->first_name;
            $data['patronymic'] = $doctor->patronymic;

            $region = Regions::findFirstByid($doctor->id_region);
            $data['region'] = $region->name;

            $data['city'] = $doctor->city;

            $specialization = Specialty::findFirstByid($doctor->id_spec);
            $data['specialization'] = $specialization->name;
        }

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'doctorData' => $data
        ), JSON_UNESCAPED_UNICODE);
    }

    /** Получение данных об аптеке*/
    public function getPharmacyDataAction(int $pharmacyId)
    {
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $data = [];

        $pharmacy = Pharmacy::findFirstByid($pharmacyId);

        if ($pharmacy) {
            $data['name'] = $pharmacy->name;
            $data['isNetworking'] = $pharmacy->networking == 1;

//            $contract = PharmacyNetwork::findFirstByid($pharmacy->contract);
//            $data['contract'] = $contract->name;
            $data['contract'] = "";

            $data['surname'] = $pharmacy->surname;
            $data['firstName'] = $pharmacy->first_name;
            $data['patronymic'] = $pharmacy->patronymic;

            $region = Regions::findFirstByid($pharmacy->id_region);
            $data['region'] = $region->name;

            $data['city'] = $pharmacy->city;
            $data['address'] = $pharmacy->address;
        }

        return json_encode(array(
            'status' => 200,
            'text' => 'ok',
            'pharmacyData' => $data
        ), JSON_UNESCAPED_UNICODE);
    }

    /** Сохраняет данные о докторе*/
    public function saveDoctorDataAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 404, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $doctorId = $this->request->getPost('doctorId', 'int');

        $doctor = Doctors::findFirstByid($doctorId);

        if (!$doctor) {
            return json_encode(array('status' => 404, 'text' => 'Доктор не найден'), JSON_UNESCAPED_UNICODE);
        }

        $surname = $this->request->getPost('surname', 'string');
        $name = $this->request->getPost('name', 'string');
        $patronymic = $this->request->getPost('patronymic', 'string');

        $doctor->setSurname($surname);
        $doctor->setFirstName($name);
        $doctor->setPatronymic($patronymic);

        if (!$doctor->update()) {
            foreach ($doctor->getMessages() as $message) {
                return json_encode(array('status' => 402, 'text' => $message), JSON_UNESCAPED_UNICODE);
            }
        }

        return json_encode(array('status' => 200, 'text' => 'Данные успешно изменены'), JSON_UNESCAPED_UNICODE);
    }

    /** Сохраняет данные об аптеке*/
    public function savePharmacyDataAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 300, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $pharmacyId = $this->request->getPost('pharmacyId', 'int');

        $pharmacy = Pharmacy::findFirstByid($pharmacyId);

        if (!$pharmacy) {
            return json_encode(array('status' => 404, 'text' => 'Аптека не найдена'), JSON_UNESCAPED_UNICODE);
        }

        $name = $this->request->getPost('name', 'string');
        $surname = $this->request->getPost('surname', 'string');
        $firstName = $this->request->getPost('first_name', 'string');
        $patronymic = $this->request->getPost('patronymic', 'string');

        $pharmacy->setName($name);
        $pharmacy->setSurname($surname);
        $pharmacy->setFirstName($firstName);
        $pharmacy->setPatronymic($patronymic);

        if (!$pharmacy->update()) {
            foreach ($pharmacy->getMessages() as $message) {
                return json_encode(array('status' => 402, 'text' => $message), JSON_UNESCAPED_UNICODE);
            }
        }

        return json_encode(array('status' => 200, 'text' => 'Данные успешно изменены'), JSON_UNESCAPED_UNICODE);
    }

    /** Отправляет план в отчет */
    public function sendInReportAction()
    {
        if (!$this->request->isPost()) return json_encode(array('status' => 405, 'text' => 'Тип запроса - POST'), JSON_UNESCAPED_UNICODE);
        if (!$this->isAuth) return json_encode(array('status' => 300, 'text' => 'Ошибка авторизации'), JSON_UNESCAPED_UNICODE);

        $planId = $this->request->getPost('planId', 'int');
        $status = $this->request->getPost('status', 'int');

        $currentPlan = Plans::findFirstById($planId);

        if (!$currentPlan) {
            return json_encode(array('status' => 404, 'text' => "Не удалось найти план"), JSON_UNESCAPED_UNICODE);
        }

        if ($currentPlan->getActivityId() == 18 && $status == 1) {
            $planId = $currentPlan->getId();

            $cntDocInPlan = ObjectInPlans::find("id_plan = $planId AND type_object = 'doctor'")->count();
            $cntRecordedInfo = RemoteVisit::find("id_plan = $planId")->count();

            if ($cntDocInPlan !== $cntRecordedInfo) {
                return json_encode(array('status' => 404, 'text' => "В данном плане остались доктора с незаполненной информацией о дистанционных визитах!"), JSON_UNESCAPED_UNICODE);
            }

        }

        if ($status < 0 || $status > 1) {
            return json_encode(array('status' => 404, 'text' => "Некорректный статус плана"), JSON_UNESCAPED_UNICODE);
        }

        $currentPlan->setReport($status);
        $currentTime = time();
        $currentPlan->setSendReportData($currentTime);

        if (!$currentPlan->update()) {
            foreach ($currentPlan->getMessages() as $message) {
                return json_encode(array('status' => 404, 'text' => $message), JSON_UNESCAPED_UNICODE);
            }
        }

        if ($status == 1)
            return json_encode(array('status' => 200, 'text' => "Отчет отправлен"), JSON_UNESCAPED_UNICODE);
        else
            return json_encode(array('status' => 200, 'text' => "Отчет возвращён в планы"), JSON_UNESCAPED_UNICODE);

    }

    private function getToken($userId, $newUserToken, string $typeDevice, string $deviceInfo): string
    {
        $exp = time() + 60 * 60 * 24;
        $token = $this->security->hash($exp . $userId);

        $expRefresh = time() + 60 * 60 * 24 * 31;
        $tokenRefresh = $this->security->hash($expRefresh . $userId);

        $newUserToken->setExp(date("Y-m-d H:i:s", $exp));
        $newUserToken->setToken($token);

        $newUserToken->setRefreshExp(date("Y-m-d H:i:s", $expRefresh));
        $newUserToken->setRefreshToken($tokenRefresh);

        if (!$newUserToken->save()) {
            return json_encode(array('status' => 500, 'text' => 'Не удалось получить токен.'), JSON_UNESCAPED_UNICODE);
        }//if

//        $DeviceUsageInstance = new DeviceusageController;
//        $DeviceUsageInstance->recordMobileDeviceUsage($userId, $typeDevice, $deviceInfo);

        return json_encode(array(
            'status' => 200,
            'text' => 'OK',
            'token' => $token,
            'exp' => $exp,
            'refreshToken' => $tokenRefresh,
            'refreshExp' => $expRefresh,
            'user_id' => $userId
        ), JSON_UNESCAPED_UNICODE);
    }
}