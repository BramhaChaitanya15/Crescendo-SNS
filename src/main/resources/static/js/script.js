//localhost can be changed to server IP for chat and notifications

//function to get element by id
function _id(name) {
	return document.getElementById(name);
}
//function to get element by class
function _class(name) {
	return document.getElementsByClassName(name);
}

//registration page javascript
//confirming password
function validateReg() {
	var passwordValue = _id("password1").value;
	var cpasswordValue = _id("password2").value;
	var valid = true;
	if (passwordValue !== cpasswordValue) {
		setError(_id("password2"));
		valid = false;
	}
	return valid;
}

//login page javascript
function validatelogin() {
	var username = _id("username").value;
	var password = _id("password").value;
	var valid = true;
	if (username === "" || username === null) {
		setError(_id("username"));
		valid = false;
	} else { setSuccess(_id("username")) }
	if (password === "" || password === null) {
		setError(_id("password"));
		valid = false;
	} else { setSuccess(_id("password")) }
	return valid;
}

//function to set error to input field
function setError(x) {
	x.className = "form-control is-invalid";
}

//function to set error to input field
function setSuccess(x) {
	x.className = "form-control is-valid";
}

//js for search
const search = () => {
	let query = _id("search-input").value;
	let res = _id("search-result");

	console.log(res);

	if (query == "") {
		res.className = "search-result";
	} else {
		console.log(query);

		//sending request to server localhost can be changed to server IP for chat and notifications
		let url = "http://localhost:8181/search/" + query;

		fetch(url).then((Response) => {
			return Response.json();
		}).then((data) => {
			let text = `<ul class="list-group">`

			data.forEach(user => {
				text += `<li class="list-group-item list-group-action list-group-item-info">
							<div class="row">
								<div class="col-md-1">
								<a class="stretched-link" href="/user/other_profile/${user.user_id}" >
									<img src="/img/profileImg/${user.profile_image_name}"  height="40px"
										width="40px" class="border border-secondary rounded-circle mt-2"/>
								</a>
								</div>
								<div class="col-md-10">
									<a class="stretched-link" href="/user/other_profile/${user.user_id}">${user.first_name} ${user.last_name}</a><br>
									<small>${user.username}</small>
								</div>
							</div>
						</li>`;
			});

			text += `</ul>`;

			res.innerHTML = text;
			res.className = "search-result active";
		});
	}
};
//end js for user search
//handle notifications
var stompClient = null;
// function for establishing web socket subscription
function connect() {
	let socket = new SockJS("http://localhost:8181/websocket");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {

		console.log("Connected : " + frame);
		console.log(frame.headers['user-name']);

		//subscribe
		stompClient.subscribe("/user/" + frame.headers['user-name'] + "/topic/return-to", function(response) {
			if (JSON.parse(response.body).type === "notification") {
				showNotification(JSON.parse(response.body));
			} else if (JSON.parse(response.body).type === "message") {
				showMessage(JSON.parse(response.body));
			}
		});
	});
}
// function to show notidications dynamically and live
var a = 0;
function showNotification(Notification) {
	const notificationsContainer = _id('notifications-container');
	const notificationsContainer1 = _id('notifications').getElementsByTagName('ul')[0];

	const notificationElement = document.createElement('div');
	const notificationElement1 = document.createElement('li');

	notificationElement.className = 'notification';
	notificationElement1.className = 'list-group-item list-group-item-warning';

	notificationElement.innerText = Notification.message;
	notificationsContainer.appendChild(notificationElement);
	if (Notification.postId != null) {
		notificationElement1.innerHTML = `<a href="/user/postpage/${Notification.postId.post_id}">
											<div class="row">
												<div class="col-md-2"><img
													src="/img/profileImg/${Notification.senderUserId.profile_image_name}"
													height="30px" width="30px" class="rounded-circle" /></div>
												<div class="col-md-10">
													<span>${Notification.message}</span><br>
													<small>${Notification.timeStampStr}</small>
												</div>
											</div>
										</a>`;
	} else {
		notificationElement1.innerHTML = `<a href="/user/profile">
											<div class="row">
												<div class="col-md-2"><img
													src="/img/profileImg/${Notification.senderUserId.profile_image_name}"
													height="30px" width="30px" class="rounded-circle" /></div>
												<div class="col-md-10">
													<span>${Notification.message}</span><br>
													<small>${Notification.timeStampStr}</small>
												</div>
											</div>
										</a>`;
	}
	notificationsContainer1.appendChild(notificationElement1);
	$('.notif .button_badge').removeAttr("hidden");
	$("#no-notif").attr("hidden", "hidden");
	$("#clearNotifBtn").removeAttr("disabled");
	a++;
	console.log(a);
	$('.notif .count').html(a);

	setTimeout(() => {
		notificationElement.style.opacity = '0';
		setTimeout(() => {
			notificationElement.remove();
		}, 500);
	}, 3000);
}
// run on window refresh
$(document).ready((e) => {
	connect();
	$(".c-link").removeClass("selected");
});
// function for sending notification to the websocket controller
function sendNotification(notification) {
	stompClient.send("/app/message", {}, JSON.stringify(notification));
}
//function to mark notifications as read
const nRead = (uid, suid) => {
	let url = "http://localhost:8181/read/" + uid + "/" + suid;
	fetch(url).then(response => {
		// Check if the response status is OK (200)
		if (!response.ok) {
			throw new Error(`HTTP error! Status: ${response.status}`);
		}

		// Parse the JSON response
		return response.json();
	}).then((data) => {
		if (data.read == "nRead") {
			$('.notif .button_badge').attr("hidden", "hidden");
			$('.notif .count').html(0);
		} else if (data.read == "mRead") {
			$('.msg .count').html(data.count);
			if (data.count === 0) {
				$('.msg .button_badge').attr("hidden", "hidden");
			}
		}
		$('.msg .count').html(data.count);
	});
};
//for deletion of all notifications
const clearNotifications = (userId) => {
	Swal.fire({
		title: 'Are you sure, you want to clear all notifications?',
		text: "You won't be able to revert this!",
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'Yes, clear all!'
	}).then((result) => {
		if (result.isConfirmed) {
			let url = "/user/clear-all-notif/" + userId;
			window.location = url;
		}
	});
}
//end handle notifications
//js to like a post
const like = (pid, likeCount) => {
	let like = $("#like" + pid);
	let unlike = $("#unlike" + pid);
	let likeno = $("#like-number" + pid);
	let plike = $("#plike" + pid);
	let punlike = $("#punlike" + pid);

	let url = "http://localhost:8181/like/" + pid;
	fetch(url).then(response => {
		// Check if the response status is OK (200)
		if (!response.ok) {
			throw new Error(`HTTP error! Status: ${response.status}`);
		}

		// Parse the JSON response
		return response.json();
	}).then((data) => {
		if (data.data == "liked") {
			sendNotification(data.notification);
			likeno.html(likeCount + 1);
			like?.attr("hidden", "hidden");
			unlike?.removeAttr("hidden");
			plike?.attr("hidden", "hidden");
			punlike?.removeAttr("hidden");
		}
	});
};

//js to unlike a post
const unlike = (pid, likeCount) => {
	let like = $("#like" + pid);
	let unlike = $("#unlike" + pid);
	let plike = $("#plike" + pid);
	let punlike = $("#punlike" + pid);
	let likeno = $("#like-number" + pid);

	let url = "http://localhost:8181/unlike/" + pid;
	fetch(url).then(Response => Response.text()).then((data) => {
		if (data == "unliked") {
			likeno.html(likeCount - 1);
			unlike?.attr("hidden", "hidden");
			like?.removeAttr("hidden");
			punlike?.attr("hidden", "hidden");
			plike?.removeAttr("hidden");
		}
	});
};

//js to save a post
const save = (pid) => {
	let save = $("#save" + pid);
	let unsave = $("#unsave" + pid);
	let psave = $("#psave" + pid);
	let punsave = $("#punsave" + pid);

	let url = "http://localhost:8181/save/" + pid;
	fetch(url).then(Response => Response.text()).then((data) => {
		if (data == "saved") {
			save?.attr("hidden", "hidden")
			unsave?.removeAttr("hidden");
			psave?.attr("hidden", "hidden")
			punsave?.removeAttr("hidden");
			showToast("Post Saved!");
		}
	});
};

//js to remove saved post
const unsave = (pid) => {
	let save = $("#save" + pid);
	let unsave = $("#unsave" + pid);
	let psave = $("#psave" + pid);
	let punsave = $("#punsave" + pid);

	let url = "http://localhost:8181/unsave/" + pid;
	fetch(url).then(Response => Response.text()).then((data) => {
		if (data == "unsaved") {
			unsave?.attr("hidden", "hidden")
			save?.removeAttr("hidden");
			punsave?.attr("hidden", "hidden")
			psave?.removeAttr("hidden");
			showToast("Post removed from saved!");
		}
	});
};

//js to follow some user
const follow = (uid) => {
	let follow = $("#follow");
	let unfollow = $("#unfollow");

	let url = "http://localhost:8181/follow/" + uid;
	fetch(url).then(response => {
		// Check if the response status is OK (200)
		if (!response.ok) {
			throw new Error(`HTTP error! Status: ${response.status}`);
		}

		// Parse the JSON response
		return response.json();
	}).then((data) => {
		if (data.data == "follow") {
			sendNotification(data.notification);
			follow.attr("hidden", "hidden");
			unfollow.removeAttr("hidden");
			showToast("You started following " + data.userFollowed);
		}
	});
};

//js to unfollow user
const unfollow = (uid) => {
	let follow = $("#follow");
	let unfollow = $("#unfollow");

	let url = "http://localhost:8181/unfollow/" + uid;
	fetch(url).then(response => {
		// Check if the response status is OK (200)
		if (!response.ok) {
			throw new Error(`HTTP error! Status: ${response.status}`);
		}

		// Parse the JSON response
		return response.json();
	}).then((data) => {
		if (data.data == "unfollow") {
			unfollow.attr("hidden", "hidden");
			follow.removeAttr("hidden");
			showToast("You stopped following " + data.userFollowed);
		}
	});
};

// js for comments and replies
const comment = (pid) => {
	var cmt = _id("comment").value;
	_class("emojionearea-editor")[0].childNodes[0].data = "";
	_class("emojionearea-editor")[0].innerHTML = "";
	let url = "http://localhost:8181/comment/" + pid + "/" + cmt;
	if (cmt === "") {
		_id("cmt-req").innerHTML = "Please write a comment";
	} else if (cmt.length > 250) {
		_id("cmt-len").innerHTML = "Please write the comment within 250 words";
	} else {
		fetch(url).then(response => {
			// Check if the response status is OK (200)
			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}
			// Parse the JSON response
			return response.json();
		}).then((data) => {
			if (data.data == "cmt") {
				sendNotification(data.notification);
				showComment(data.PostComment);
				_id("comment").value = "";
			}
		});
	}
};
//show comment dynamically
function showComment(cmt) {
	console.log(cmt);
	let commentContainer = _id("comment-container");
	const commentElement = document.createElement('div');

	commentElement.className = 'row mt-2';

	commentElement.innerHTML = `<div class="col-md-10 pl-5">
									<div class="cmt">
										<strong>${cmt.user.username}</strong>
										<small class="text-muted">${cmt.date_str}</small>
										<p>${cmt.comment}</p>
									</div>
									<small class="btn form-text text-muted ml-4 reply-btn"
										data-toggle="collapse"
										data-target="#collapseExample${cmt.comment_id}"
										aria-expanded="false"
										aria-controls="collapseExample${cmt.comment_id}">reply
										<i style="font-size: 18px;" class='bx bx-reply'></i></small>
										<small class="btn form-text text-muted ml-4"
												onclick="deleteComment(${cmt.comment_id})">delete
												<i class="fa-regular fa-trash-can"></i>
										</small>
									<div class="collapse" id="collapseExample${cmt.comment_id}">
										<div class="card reply-card p-2">
											<form class="form-inline my-2 my-lg-0">
												<textarea class="form-control mr-sm-2 flex-grow-1"
														placeholder="write your reply here..."
														required></textarea>
													<button type="submit"
														class="btn btn-success my-2 my-sm-0">
														<i class="fa-solid fa-paper-plane"></i>
													</button>
											</form>
										</div>
									</div>
								</div>
								<div class="col-md-2">
									<img src="/img/profileImg/${cmt.user.profile_image_name}"
											height="40px" width="40px" />
								</div>`;

	commentContainer.appendChild(commentElement);
}
//show replies dynamically
const commentReply = (pid, cmtid) => {
	var reply = _id("reply" + cmtid).value;
	let url = "http://localhost:8181/comment-reply/" + pid + "/" + cmtid + "/" + reply;
	if (reply === "") {
		_id("reply-req").innerHTML = "Please write a reply";
	} else if (reply.length > 250) {
		_id("reply-len").innerHTML = "Please write the reply within 250 words";
	} else {
		fetch(url).then(response => {
			// Check if the response status is OK (200)
			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}
			// Parse the JSON response
			return response.json();
		}).then((data) => {
			if (data.data == "reply") {
				sendNotification(data.notification);
				_id("reply" + cmtid).value = "";
				showReply(data.reply, cmtid);
			}
		});
	}
};

function showReply(r, cmtid) {
	let replyContainer = _id("reply-container" + cmtid);
	const replyElement = document.createElement('div');

	replyElement.className = 'row cmt m-3';

	replyElement.innerHTML = `<strong>${r.user.username}</strong>
								<small class="text-muted">${r.date_str}</small><br>
								<p>${r.reply}</p>`;

	replyContainer.appendChild(replyElement);
}
//end js for comments and replies

//toggle sidebar
var sidebarBtn = document.querySelector("#sidebar-btn");
sidebarBtn?.addEventListener("click", function() {
	document.getElementById("toggle").classList.toggle("active");
})

//js for messages chat page
//search for followers to chat
function chatSearch() {
	let query = _id("chat-search-input").value;
	let res = _id("follower-search-list");
	if (query == "") {
		res.classList.remove("f-search");
	} else {

		//sending request to server
		let url = "http://localhost:8181/search/" + query;

		fetch(url).then((Response) => {
			return Response.json();
		}).then((data) => {
			let text = `<ul class="list-group">
							<h5>SEARCH LIST</h5>`

			data.forEach(sUser => {
				text += `<li class="c-link list-group-item list-group-item-success chat-list"
								onclick="openChat(${sUser.user_id},this); nRead(${user.user_id},${sUser.user_id})">
								<div class="row">
									<div class="col-md-2">
										<img src="/img/profileImg/${sUser.profile_image_name}"
											height="40px" width="40px"
											class="border border-secondary rounded-circle mt-2" />
									</div>
									<div class="col-md-8">
										<strong>
											${sUser.first_name} ${sUser.last_name}
										</strong><br>
										<small>${sUser.username}</small>
									</div>
									<div class="col-md-2 p-3">
										<i class="fa-solid fa-circle-dot" id="${sUser.username}"
											hidden></i>
									</div>
								</div>
							</li>`;
			});

			text += `</ul>`;
			res.innerHTML = text;

			res.classList.add("f-search");
		});
	}
}
//function to open chatbox
function openChat(id, temp) {
	//for chatbox  change
	$(".c-link").removeClass("selected");
	$.ajax({
		url: "http://localhost:8181/user/chat-box/" + id,
		success: function(data, textStatus, jqXHR) {
			$(".loader").hide();
			$("#chat-container").html(data);
			$(temp).addClass("selected");
			$("#message").emojioneArea({
				pickerPosition: "top",
				inline: true
			});
		}
	});
}
//function to close chat box
function closeChat(fid) {
	$(".c-link").removeClass("selected");
	$("#chat-home" + fid).hide();
	$(".loader").show();
}
//function to delete chat
function deleteChat(id) {
	Swal.fire({
		title: 'Are you sure, you want to delete this chat?',
		text: "You won't be able to revert this!",
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'Yes, delete it!'
	}).then((result) => {
		if (result.isConfirmed) {
			window.location = "/user/delete-chat/" + id;
		}
	});
}
//functions relating to real time chat
function sendMessage(uid) {
	var msg = _id("message")?.value;
	const container = _id("message-container" + uid.user_id);
	const messageElement = document.createElement('div');
	if (msg == "") {
		alert("you can't send an empty message");
	} else {
		//emptying the text area
		_class("emojionearea-editor")[0].childNodes[0].data = "";
		_class("emojionearea-editor")[0].innerHTML = "";

		const notification = {
			"message": msg,
			"type": "message",
			"senderUserId": user,
			"reciepientUserId": uid
		}
		stompClient.send("/app/message", {}, JSON.stringify(notification));
		messageElement.className = 'row mt-3 justify-content-end';
		messageElement.innerHTML = `<div class="col-md-6">
								<div speech-bubble pright atop style="--bbColor:#45c5e0">
									<div>`+ msg + `</div>
								</div>
							</div>
							<div class="col-md-1">
								<img src="/img/profileImg/${user.profile_image_name}" height="25px"
									width="25px" class="border border-secondary rounded-circle mt-2" />
							</div>`;
		container?.appendChild(messageElement);
		if (container != null) {
			nRead(notification.reciepientUserId.user_id, notification.senderUserId.user_id);
		}
		msg = "";
	}
}
var c = 0;
//function to show message received
function showMessage(message) {
	const container = _id("message-container" + message.senderUserId.user_id);
	const messageElement = document.createElement('div');
	messageElement.className = 'row mt-3 justify-content-start';
	messageElement.innerHTML = `<div class="col-md-1">
								<img src="/img/profileImg/${message.senderUserId.profile_image_name}" height="25px"
									width="25px" class="border border-secondary rounded-circle mt-2" />
							</div>
							<div class="col-md-6">
								<div speech-bubble pleft atop style="--bbColor:#cccccc">
									<div>${message.message}</div>
								</div>
							</div>`;
	container?.appendChild(messageElement);
	c++;
	if (container == null) {
		$('#' + message.senderUserId.username)?.removeAttr("hidden");
		$('.msg .button_badge').removeAttr("hidden");
		$('.msg .count').html(c);
	} else {
		nRead(message.reciepientUserId.user_id, message.senderUserId.user_id);
	}
}
//get all online users
function fetchOnlineUsers() {
	fetch('/users/all-online')
		.then(response => response.json())
		.then(data => {
			// Update the UI with the list of online users
			for (i = 0; i < data.onlineUsers?.length; i++) {
				$("#online" + data.onlineUsers[i]?.user_id)?.removeAttr("hidden");
				$("#online-dot" + data.onlineUsers[i]?.user_id)?.removeAttr("hidden");
			}
			for (i = 0; i < data.offlineUser?.length; i++) {
				$("#online" + data.offlineUser[i]?.user_id)?.attr("hidden", "hidden");
				$("#online-dot" + data.offlineUser[i]?.user_id)?.attr("hidden", "hidden");
			}
		});
}

// Call fetchOnlineUsers every 5 seconds
setInterval(fetchOnlineUsers, 5000);
//end js for messages chat page

//js for report form
const subTypes = {
	inappropriate: [
		"Sexual Content",
		"Violent Content",
		"Hate Speech",
		"Harassment or Bullying"
	],
	misinformation: [
		"False Information",
		"Health Misinformation",
		"Political Misinformation"
	],
	spam: [
		"Commercial Spam",
		"Phishing",
		"Repeated Posts"
	],
	selfHarm: [
		"Self-Harm Encouragement",
		"Suicidal Content",
		"Eating Disorders Promotion"
	],
	terrorism: [
		"Terrorist Propaganda",
		"Recruitment for Terrorist Activities",
		"Support for Organized Crime"
	],
	offensive: [
		"Profanity",
		"Obscenities",
		"Insults or Name-calling"
	],
	privacy: [
		"Sharing Private Information",
		"Impersonation"
	],
	others: [
		"Off-topic Content",
		"Not in English",
		"Doesn't belong here",
		"Other"
	]
};

var step1 = _id("step1");
var step2 = _id("step2");
var step3 = _id("step3");

var progress = _id("progress");

function showSubTypes() {
	const reportType = _id("reportType").value;
	const subTypeSelect = _id("subType");

	// Clear previous options
	subTypeSelect.innerHTML = '<option value="" disabled selected>Select...</option>';

	// Populate new subtypes
	subTypes[reportType].forEach(subType => {
		const option = document.createElement("option");
		option.value = subType.toLowerCase().replace(/ /g, "_");
		option.text = subType;
		subTypeSelect.appendChild(option);
	});

	// Move to the next step
	step1.style.left = "-450px";
	step2.style.left = "40px";
	progress.style.width = "180px";
}

function moveToPreviousStep1() {
	// Move to the previous step
	step1.style.left = "40px";
	step2.style.left = "450px";
	progress.style.width = "90px";
}

function showOptional() {
	// Move to the previous step
	step2.style.left = "-450px";
	step3.style.left = "40px";
	progress.style.width = "300px";
}

function moveToPreviousStep2() {
	// Move to the next step
	step2.style.left = "40px";
	step3.style.left = "450px";
	progress.style.width = "180px";
}

//submit report
function reportPost(pid) {
	$("#pid").val(pid);
}
function submitReport() {
	var pid = $("#pid").val();
	var reportType = $("#reportType").val();
	var reportSubType = $("#subType").val();
	var reportOptional = ($("#feedback").val()) ? $("#feedback").val() : "empty";

	const url = "http://localhost:8181/submit-report/"
		+ pid + "/" + reportType + "/" + reportSubType
		+ "/" + reportOptional;

	const formReset = $("#report-form-reset");
	const formClose = $("#close-report-modal");

	fetch(url).then(response => response.text()).then((data) => {
		if (data == "reported") {
			formReset.click();
			formClose.click();
			moveToPreviousStep2();
			moveToPreviousStep1();
			showToast("Post Reported!");
		}
	});
}
//end js for report form

//js to show toast
function showToast(content) {
	$("#toast").addClass("display");
	$("#toast").html(content);
	setTimeout(() => {
		$("#toast").removeClass("display");
	}, 2000);

}
