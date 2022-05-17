<?php
include_once "../../php/Utils/RequestAPI.php";
include_once "../../php/Utils/Response.php";
include_once "../../php/Database/PDOController.php";
include_once "../../php/Utils/DataStream.php";
include_once "../../php/Utils/SessionUtils.php";

$method = RequestAPI::getMethod();

function registerUser(){
    if(RequestAPI::getMethod() != "POST"){
        return Response::message("Wrong method",405);
    }

    $data = RequestAPI::getJSON();

    $similarUsers = PDOController::getCommand("SELECT * FROM bam_users WHERE login = :login OR email = :email",[
        'login'=>$data['login'],
        'email'=>$data['email']
    ]);

    if(count($similarUsers) != 0) {
        return Response::message("Login or email is busy");
    }

    PDOController::putCommand("INSERT INTO bam_users (userId, login, password, email, registerDate) VALUES (NULL, :login, :password, :email, NOW())",[
        'login'=>$data['login'],
        'password'=>md5($data['password']),
        'email'=>$data['email']
    ]);

    return Response::message("User registered correctly");
};

echo registerUser();
