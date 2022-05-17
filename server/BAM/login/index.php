<?php
include_once "../../php/Utils/RequestAPI.php";
include_once "../../php/Utils/Response.php";
include_once "../../php/Database/PDOController.php";
include_once "../../php/Utils/DataStream.php";
include_once "../../php/Utils/SessionUtils.php";

$method = RequestAPI::getMethod();

function loginUser(){
    if(RequestAPI::getMethod() != "POST"){
        http_response_code(400);
        return;
    }
    if(!(isset($_POST["login"]) && isset($_POST['password']))){
        http_response_code(400);
        return;
    }
    $hashedPassword = md5($_POST['password']);
    $matchingUser = PDOController::getCommand(
        "SELECT userId, login, password FROM bam_users WHERE login = :login AND password = :password",
        ["password"=>$hashedPassword,"login"=>$_POST['login']]
    );

    if(count($matchingUser)>0){
        $_SESSION['userId'] = $matchingUser[0]['userId'];
        $_SESSION['username'] = $matchingUser[0]['login'];
        return Response::message("Login successful");
    } else {
        http_response_code(204);
        return Response::message("Login and password are not matching");
    }
};

echo loginUser();
