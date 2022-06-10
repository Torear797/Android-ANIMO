<?php

class UserTokens extends \Phalcon\Mvc\Model
{

    /**
     *
     * @var integer
     */
    protected $id;

    /**
     *
     * @var integer
     */
    protected $user_id;

    /**
     *
     * @var string
     */
    protected $token;

    /**
     *
     * @var string
     */
    protected $exp;


    /**
     *
     * @var string
     */
    protected $refreshToken;

    /**
     *
     * @var string
     */
    protected $refresh_exp;

    /**
     *
     * @var string
     */
    protected $device_id;

    /**
     * @return string
     */
    public function getRefreshToken(): string
    {
        return $this->refreshToken;
    }

    /**
     * @param string $refreshToken
     */
    public function setRefreshToken(string $refreshToken): void
    {
        $this->refreshToken = $refreshToken;
    }

    /**
     * @return string
     */
    public function getRefreshExp(): string
    {
        return $this->refresh_exp;
    }

    /**
     * @param string $refresh_exp
     */
    public function setRefreshExp(string $refresh_exp): void
    {
        $this->refresh_exp = $refresh_exp;
    }


    /**
     * Method to set the value of field id
     *
     * @param integer $id
     * @return $this
     */
    public function setId($id)
    {
        $this->id = $id;

        return $this;
    }

    /**
     * Method to set the value of field user_id
     *
     * @param integer $user_id
     * @return $this
     */
    public function setUserId($user_id)
    {
        $this->user_id = $user_id;

        return $this;
    }

    /**
     * Method to set the value of field token
     *
     * @param string $token
     * @return $this
     */
    public function setToken($token)
    {
        $this->token = $token;

        return $this;
    }

    /**
     * Method to set the value of field exp
     *
     * @param string $exp
     * @return $this
     */
    public function setExp($exp)
    {
        $this->exp = $exp;

        return $this;
    }

    /**
     * Method to set the value of field device_id
     *
     * @param string $device_id
     * @return $this
     */
    public function setDeviceId($device_id)
    {
        $this->device_id = $device_id;

        return $this;
    }

    /**
     * Returns the value of field id
     *
     * @return integer
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Returns the value of field user_id
     *
     * @return integer
     */
    public function getUserId()
    {
        return $this->user_id;
    }

    /**
     * Returns the value of field token
     *
     * @return string
     */
    public function getToken()
    {
        return $this->token;
    }

    /**
     * Returns the value of field exp
     *
     * @return string
     */
    public function getExp()
    {
        return $this->exp;
    }

    /**
     * Returns the value of field device_id
     *
     * @return string
     */
    public function getDeviceId()
    {
        return $this->device_id;
    }

    /**
     * Initialize method for model.
     */
    public function initialize()
    {
        $this->setSchema("animo_su");
        $this->setSource("user_tokens");
        $this->belongsTo('user_id', 'Users', 'id', ['alias' => 'Users']);
    }

    /**
     * Returns table name mapped in the model.
     *
     * @return string
     */
    public function getSource()
    {
        return 'user_tokens';
    }

    /**
     * Allows to query a set of records that match the specified conditions
     *
     * @param mixed $parameters
     * @return UserTokens[]|UserTokens|\Phalcon\Mvc\Model\ResultSetInterface
     */
    public static function find($parameters = null)
    {
        return parent::find($parameters);
    }

    /**
     * Allows to query the first record that match the specified conditions
     *
     * @param mixed $parameters
     * @return UserTokens|\Phalcon\Mvc\Model\ResultInterface
     */
    public static function findFirst($parameters = null)
    {
        return parent::findFirst($parameters);
    }

    public function isActive()
    {
        return strtotime(date($this->getExp())) > time();
    }

    public function isAuth(): bool
    {
        if ($this->token && $this->isActive()) return true;

        return false;
    }

    public function isRefreshTokenActive()
    {
        return strtotime(date($this->getRefreshExp())) > time();
    }

    public function refreshToken()
    {
        $this->setExp(date("Y-m-d H:i:s", time() + 60 * 60 * 24));
        $this->save();
    }
}
