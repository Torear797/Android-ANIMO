<?php

use Phalcon\Mvc\Controller;
use Phalcon\Mvc\Dispatcher;

class ControllerBase extends Controller
{
	public function authorized(){

		if(!$this->isLoggedIn()){
            $route = $this->router->getControllerName().'/'.$this->router->getActionName();

            $route = $route !== '/' ? "user/login?from=$route" : "user/login";

            return $this->response->redirect($route)->send();
		}//if

	}//func authorized

    public function isLoggedInMobile($userToken)
    {
        if ($userToken && $userToken->isActive()) {
            return true;
        }

        return false;
    }
	
	public function isLoggedIn(){
		//check if the variables defined 
		if($this->session->has('AUTH_ID') AND $this->session->has('AUTH_NAME')){
			return true;
		}//if
		return false;
	}// func isLoggedIn

	/**
	 * Метод получает из названия таблицы название модели
	 * 
	 * @param string $tableName название таблицы БД
	 * @return string название модели
	 */
	public function getModelName($tableName): string
	{	
		$modelName = '';

		if (empty(trim($tableName))) {
			return $modelName;
		}//if

		$chunks = explode('_', $tableName);

		foreach ($chunks as $c) {
			$modelName .= ucfirst($c);
		}//foreach

		return $modelName;
	}//public function getModelName

	/**
	 * Метод производит форматирование номера телефона 
	 *
	 * @param string $phoneNumber 
	 * @return string Номер приведенный к формату системы
	 */
	public function formatPhoneNumber(string $phoneNumber): string
	{	
		$len = strlen($phoneNumber);

		if ($len === 11) {
			$phoneNumber = $this->processMobileNubmer($phoneNumber);
		} else if ($len === 10) {
			$phoneNumber = "(".substr($phoneNumber,0,3).") ".substr($phoneNumber,3,3)."-".substr($phoneNumber,6,2)."-".substr($phoneNumber,8,2);
		} else if ($len === 9) {
			$phoneNumber = "(".substr($phoneNumber,0,3).") ".substr($phoneNumber,3,2)."-".substr($phoneNumber,5,2)."-".substr($phoneNumber,7,2);
		} else if ($len === 7) {
			$phoneNumber = substr($phoneNumber,0,3)."-".substr($phoneNumber,3,2)."-".substr($phoneNumber,5,2);
		} else if ($len === 6) {
			$phoneNumber = substr($phoneNumber,0,2)."-".substr($phoneNumber,2,2)."-".substr($phoneNumber,4,2);
		} else {
			$phoneNumber = '';
		}//else

		return $phoneNumber;
	}//public function formatPhoneNumber

	/**
	 * Метод форматирует мобильный номер телефона
	 *
	 * @param string $phone
	 * @return string
	 */
	private function processMobileNubmer(string $phone): string
	{	
		$firstNum = $phone[0];

		if ($firstNum === '8') {
			$phoneNumber = substr($phone,0,1)." (".substr($phone,1,3).") ".substr($phone,4,3)."-".substr($phone,7,2)."-".substr($phone,9,2);
		} else if ($firstNum === '7') {
			$phoneNumber = "+".substr($phone,0,1)." (".substr($phone,1,3).") ".substr($phone,4,3)."-".substr($phone,7,2)."-".substr($phone,9,2);
		} else {
			$phoneNumber = '';
		}//else

		return $phoneNumber;
	}//private function processMobileNubmer

	public function getDeviceInfo(Mobile_Detect $mobileDetect):string {
		$deviceInfo = '';
		$phoneDevices = $mobileDetect->getPhoneDevices();
		$tabletDevices = $mobileDetect->getTabletDevices();

		$devices = array_merge($phoneDevices, $tabletDevices);

		foreach ($devices as $device => $value) {
			$method = 'is'.$device;

			if ($mobileDetect->$method()) {
				$deviceInfo = $device;
			}//if

		}//foreach

		return $deviceInfo;
	}//protected function getMobileDevice

	public function getFullUrl(string $appendUrl):string {
		$host = $_SERVER['HTTP_HOST'];
        $protocol = $_SERVER['PROTOCOL'] = isset($_SERVER['HTTPS']) && !empty($_SERVER['HTTPS']) ? 'https' : 'http';

		return $protocol."://".$host."/".$appendUrl;
	}//public function getFullUrl

	public function fileErrorCodeToMessage(int $code) :string{

        switch ($code) {
            case UPLOAD_ERR_INI_SIZE:
                $message = "Ошибка! Размер принятого файла превысил максимально допустимый размер.";
                break;
            case UPLOAD_ERR_FORM_SIZE:
                $message = "Ошибка! Размер загружаемого файла превысил максимальное значение.";
                break;
            case UPLOAD_ERR_PARTIAL:
                $message = "Ошибка! Загружаемый файл был получен только частично.";
                break;
            case UPLOAD_ERR_NO_FILE:
                $message = "Ошибка! Файл не был загружен.";
                break;
            case UPLOAD_ERR_NO_TMP_DIR:
                $message = "Ошибка! Отсутствует временная папка.";
                break;
            case UPLOAD_ERR_CANT_WRITE:
                $message = "Ошибка! Не удалось записать файл на диск.";
                break;
            case UPLOAD_ERR_EXTENSION:
                $message = "Ошибка! PHP-расширение остановило загрузку файла.";
                break;

            default:
                $message = "Неизвестная ошибка при загрузке файла!";
                break;
        }//switch

        return $message;
    }//private function failCodeToMessage

	public function cellColor($objPHPExcel,$cells,$color){
   	 	$objPHPExcel->getActiveSheet()->getStyle($cells)->getFill()->applyFromArray(array(
        	'type' => PHPExcel_Style_Fill::FILL_SOLID,
        	'startcolor' => array(
             'rgb' => $color
        )
    	));
	}//function cellColor

// @ $data array('total' => count,'active' => count, 'no_active' => count)
// return string countTotal/countActive/countNotActive as object PHPExcel_RichText
	public function multiColorExcel($data, $extend = "false") {
		
		$objRichText = new PHPExcel_RichText();
		$p1 = $objRichText->createTextRun($data['total']);
		$p1->getFont()->setColor(new PHPExcel_Style_Color('FF000000'));
		$p1->getFont()->setName('Arial')->setSize(8);

		if ($extend == "true") {
			$p2 = $objRichText->createTextRun($data['active']);
			$p2->getFont()->setColor(new PHPExcel_Style_Color('FF05840B'));
			$p2->getFont()->setName('Arial')->setSize(8);

			$p3 = $objRichText->createTextRun($data['no_active']);
			$p3->getFont()->setColor(new PHPExcel_Style_Color('FFFF6000'));
			$p3->getFont()->setName('Arial')->setSize(8);
		}

		return $objRichText;
	}//function multiColorExcel

	// @ $data array('plan_on_count_total' => count,'percent_complete_total' => count)
	// return string planOnCountTotal/percentCompleteTotal object PHPExcel_RichText
	public function multiColorPlanExcel($data) {
		
		$objRichText = new PHPExcel_RichText();
		$p1 = $objRichText->createTextRun($data['plan']);
		$p1->getFont()->setColor(new PHPExcel_Style_Color('0066CC'));
		$p1->getFont()->setName('Arial')->setSize(8);

		$p2 = $objRichText->createTextRun(' / ');
		$p2->getFont()->setColor(new PHPExcel_Style_Color('FF000000'));
		$p2->getFont()->setName('Arial')->setSize(8);

		$p3 = $objRichText->createTextRun($data['percent_complete']);
		$p3->getFont()->setColor(new PHPExcel_Style_Color('008000'));
		$p3->getFont()->setName('Arial')->setSize(8);
			
		return $objRichText;
	}//function multiColorExcel

}//class ControllerBase
